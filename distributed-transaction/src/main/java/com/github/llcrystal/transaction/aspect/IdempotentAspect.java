package com.github.llcrystal.transaction.aspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.llcrystal.transaction.Idempotent;
import com.github.llcrystal.transaction.aspect.config.TransactionProperties;
import com.github.llcrystal.transaction.domain.aggregate.transaction.TransactionItem;
import com.github.llcrystal.transaction.domain.aggregate.transaction.service.IdempotentService;
import com.github.llcrystal.transaction.domain.TransactionException;
import com.github.llcrystal.transaction.domain.aggregate.transaction.entity.IdempotentEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class IdempotentAspect {
    private final TransactionProperties transactionProperties;
    private final IdempotentService idempotentService;

    @PostConstruct
    public void init() {
        if (transactionProperties == null || transactionProperties.getTransaction() == null || transactionProperties.getTransaction().isEmpty()) {
            log.error("Transaction properties did not config!");
            throw new TransactionException("Transaction properties did not config!");
        }
    }

    @Pointcut("@annotation(com.github.llcrystal.transaction.Idempotent)")
    public void invokePointcut() {
    }

    @Around("invokePointcut()")
    public Object invokeAround(ProceedingJoinPoint joinPoint) {
        Object[] params = joinPoint.getArgs();
        Method invokeMethod = getInvokeMethod(joinPoint);
        Idempotent annotation = invokeMethod.getDeclaredAnnotation(Idempotent.class);

        TransactionItem transactionItem = new TransactionItem(TransactionItem.parseIdempotentKey(params));
        transactionItem.validateInvoke(transactionProperties, annotation.invoke());

        Optional<IdempotentEntity> invokeHistory = idempotentService.findByIdempotentKey(transactionItem.getIdempotentKey().getIdempotentKey(annotation.invoke()));
        if (invokeHistory.isPresent() && invokeHistory.get().isInvokeSuccess()) {
            log.info("Idempotent triggered, method name is {}", annotation.invoke());
            return parseFromResponse(invokeMethod, invokeHistory.get().getInvokeResponse());
        }

        try {
            Object result = joinPoint.proceed();
            if (invokeHistory.isPresent()) {
                transactionItem.updateInvokeHistory(true, Serialization.serialize(result), invokeHistory.get());
                idempotentService.update(transactionItem.getInvokeHistory());
            } else {
                insertSuccessIdempotentRecord(annotation, transactionItem, Serialization.serialize(result));
            }

            // If the last step of the transaction is successful, then clean up the data records related to this transaction.
            if (transactionItem.isLastStep(annotation.invoke(), transactionProperties)) {
                idempotentService.clearIdempotentRecords(transactionItem.getTransactionName());
            }
            return result;
        } catch (Throwable e) {
            Map<String, String> masseageMap = new HashMap<>();
            masseageMap.put("message", e.getMessage() != null ? e.getMessage() : "Internal error.");
            if (invokeHistory.isPresent()) {
                transactionItem.updateInvokeHistory(false, Serialization.serialize(masseageMap), invokeHistory.get());
                idempotentService.update(transactionItem.getInvokeHistory());
            } else {
                idempotentService.save(transactionItem.createFailedEntity(annotation, Serialization.serialize(masseageMap)));
            }
            throw new TransactionException(e);
        }
    }

    private void insertSuccessIdempotentRecord(Idempotent annotation, TransactionItem transactionItem, String response) {
        // Second confirmation to resolve the issue of incorrect results being returned first in a multi-thread concurrent situation.
        Optional<IdempotentEntity> invokeNewHistory = idempotentService.findByIdempotentKey(transactionItem.getIdempotentKey().getIdempotentKey(annotation.invoke()));
        if (invokeNewHistory.isEmpty()) {
            idempotentService.save(transactionItem.createSuccessEntity(annotation, response));
        } else {
            transactionItem.updateInvokeHistory(true, response, invokeNewHistory.get());
            idempotentService.update(transactionItem.getInvokeHistory());
        }
    }

    private Object parseFromResponse(Method invokeMethod, String value) {
        JavaType javaType = TypeFactory.defaultInstance().constructType(invokeMethod.getGenericReturnType());
        if (invokeMethod.getReturnType().isAssignableFrom(Optional.class)) {
            return Optional.of(Serialization.deserializeObject(value, javaType.getBindings().getBoundType(0)));
        } else {
            return Serialization.deserializeObject(value, javaType);
        }
    }

    private static Method getInvokeMethod(ProceedingJoinPoint joinPoint) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();

        String methodName = joinPoint.getSignature().getName();
        Method objMethod = null;
        try {
            objMethod = targetClass.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            log.warn("IdempotentAspect getInvokeMethod error:Idempotent invoke method annotation get error.");
            throw new TransactionException("Idempotent invoke method annotation get error!");
        }
        return objMethod;
    }
}
