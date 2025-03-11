package com.github.llcrystal.transaction.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.llcrystal.transaction.domain.TransactionException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class Serialization {

    private Serialization() {}

    public static String serialize(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(new Jdk8Module());
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Parse Object to Json exception:", e);
            throw new TransactionException("Idempotent invoke result serialize error!");
        }
    }

    public static Object deserializeObject(String value, JavaType javaType) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(new Jdk8Module());
        try {
            return objectMapper.readValue(value, javaType);
        } catch (JsonProcessingException e) {
            log.error("Parse Json to Object exception:", e);
            throw new TransactionException("Idempotent invoke result deserialize error!");
        }
    }
}
