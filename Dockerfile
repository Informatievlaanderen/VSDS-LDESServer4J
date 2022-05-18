# syntax=docker/dockerfile:1

FROM alpine:latest AS bom
RUN apk update && apk add git maven
RUN git clone https://github.com/Informatievlaanderen/VSDS-LDES.git vsds
WORKDIR /vsds
RUN mvn clean install -DskipFormatCode=true


#FROM maven:3.8.5-openjdk-18 AS builder
COPY . /ldes-server
WORKDIR /ldes-server
RUN mvn clean package -DskipFormatCode=true

FROM openjdk:18-ea-alpine
COPY --from=builder /ldes-server/ldes-server-application/target/ldes-server-application.jar ./
COPY --from=builder /ldes-server/ldes-server-infra-mongo/mongo-repository-spring-boot-autoconfigure/target/mongo-repository-spring-boot-autoconfigure-jar-with-dependencies.jar ./plugins/
CMD ["java", "-cp", "ldes-server-application.jar", "-Dloader.path=plugins/", "org.springframework.boot.loader.PropertiesLauncher"]
