# ll distributed transaction

## Introduction
A stateless distributed transaction solution.

## Overview
- [Dependency](#dependency)
- [Configuration](#configuration)
- [Usage](#usage)
- [Notes](#notes)
- [License](#license)

## Dependency
To use the ll distributed transaction, add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.github.llcrystal</groupId>
    <artifactId>distributed-transaction</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

For Gradle users, add the following line to your `build.gradle` file:

```groovy
implementation 'com.github.llcrystal:distributed-transaction:0.0.1-SNAPSHOT'
```

## Configuration
To configure the ll distributed transaction, add the necessary properties to your `application.properties` or `application.yml` file.

### application.properties

```properties
ll.transaction.MY_TRANSACTION_1.key1[0]=IDEMPOTENT_STEP1
ll.transaction.MY_TRANSACTION_1.key1[1]=IDEMPOTENT_STEP2
ll.transaction.MY_TRANSACTION_1.key1[2]=IDEMPOTENT_STEP3
```

### application.yml

```yaml
ll:
  transaction:
    MY_TRANSACTION_1:
      - IDEMPOTENT_STEP1
      - IDEMPOTENT_STEP2
      - IDEMPOTENT_STEP3
```

## Usage
Here's how to use the ll distributed transaction in your application.

### Step 1: Enable the Starter
Annotate your main application class with `@EnableDistributedTransaction`.

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.CustomStarter;

@EnableDistributedTransaction
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### Step 2: Inject and Use the Service
Inject the service provided by the custom starter into your application components.

Define a transaction consisting of multi-step calls. Use @Idempotent to ensure idempotent call of one step.

If any of the invoke history contain the required result, the result is returned and the method is not invoked.

```java
IdempotentKey key = new IdempotentKey("user", orderId, "MY_TRANSACTION_1");

@Idempotent(invoke = "IDEMPOTENT_STEP1")
public void createOrder(IdempotentKey key, String orderId, BigDecimal productQuantity) {
        this.order = Order.createOrder(orderId, this.product, this.price, productQuantity);
}
```


## Notes
1、This component stores the results of each idempotent step of the transaction in the JVM's local memory. 
   If you intend to use it in a production environment, it is recommended to replace it with Redis or a database. 
   Otherwise, the results of the small steps will be lost when the service restarts.

2、If idempotent steps and the caller are in the same class, it may cause the annotation to become ineffective. 
   You can use AopContext.currentProxy() to obtain the proxy object to call the corresponding method.


## License
This project is licensed under the Apache License. See the [LICENSE](LICENSE) file for details.