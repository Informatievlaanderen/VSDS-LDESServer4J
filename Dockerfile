# syntax=docker/dockerfile:1

FROM maven:3.8.5-openjdk-18 AS builder
RUN git clone https://github.com/Informatievlaanderen/VSDS-LDES.git
WORKDIR /VSDS-LDES
RUN git checkout 69356992e012803b5fa4fb13a7bcfdf110ffcf55
RUN mvn clean install

RUN mkdir /ldes-server
WORKDIR /ldes-server
COPY . /ldes-server
RUN mvn clean install -DskipFormatCode=true

FROM openjdk:18-ea-alpine
COPY --from=builder /ldes-server/ldes-server-application/target/ldes-server-application.jar ./
COPY --from=builder /ldes-server/ldes-server-infra-mongo/mongo-repository-spring-boot-autoconfigure/target/mongo-repository-spring-boot-autoconfigure-jar-with-dependencies.jar ./plugins/
COPY --from=builder /ldes-server/ldes-server-port-publication-rest/target/ldes-server-port-publication-rest-jar-with-dependencies.jar ./plugins/
CMD ["java", "-cp", "ldes-server-application.jar", "-Dloader.path=plugins/", "org.springframework.boot.loader.PropertiesLauncher"]
