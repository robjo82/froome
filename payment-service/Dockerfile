FROM maven:3.9.6 AS build

COPY pom.xml /build/pom.xml
COPY product-service/pom.xml /build/product-service/pom.xml
COPY user-service/pom.xml /build/user-service/pom.xml
COPY order-service/pom.xml /build/order-service/pom.xml
COPY payment-service/pom.xml /build/payment-service/pom.xml

WORKDIR /build
RUN mvn dependency:go-offline

COPY product-service/src /build/product-service/src
COPY user-service/src /build/user-service/src
COPY order-service/src /build/order-service/src
COPY payment-service/src /build/payment-service/src

WORKDIR /build
RUN mvn clean package -DskipTests -pl payment-service

FROM openjdk:21-jdk

RUN mkdir /app

COPY --from=build /build/payment-service/target/payment-service-*.jar /app/payment-service.jar

RUN groupadd -r apppayment && useradd -r -g apppayment apppayment
RUN chown -R apppayment:apppayment /app

USER apppayment

WORKDIR /app

ENTRYPOINT ["java","-jar","payment-service.jar"]