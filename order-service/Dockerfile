FROM maven:3.9.6 AS build

COPY pom.xml /build/pom.xml
COPY product-service/pom.xml /build/product-service/pom.xml
COPY user-service/pom.xml /build/user-service/pom.xml
COPY order-service/pom.xml /build/order-service/pom.xml
COPY payment-service/pom.xml /build/payment-service/pom.xml
COPY config-service/pom.xml /build/config-service/pom.xml
COPY eureka-service/pom.xml /build/eureka-service/pom.xml
COPY gateway-service/pom.xml /build/gateway-service/pom.xml

WORKDIR /build
RUN mvn dependency:go-offline

COPY product-service/src /build/product-service/src
COPY user-service/src /build/user-service/src
COPY order-service/src /build/order-service/src
COPY payment-service/src /build/payment-service/src
COPY config-service/src /build/config-service/src
COPY eureka-service/src /build/eureka-service/src
COPY gateway-service/src /build/gateway-service/src

WORKDIR /build
RUN mvn clean package -DskipTests -pl order-service

FROM openjdk:21-jdk

RUN mkdir /app

COPY --from=build /build/order-service/target/order-service-*.jar /app/order-service.jar

RUN groupadd -r apporder && useradd -r -g apporder apporder
RUN chown -R apporder:apporder /app

USER apporder

WORKDIR /app

ENTRYPOINT ["java","-jar","order-service.jar"]