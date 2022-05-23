# syntax=docker/dockerfile:1

FROM maven:3.8.5-openjdk-18 AS builder
RUN git clone https://github.com/Informatievlaanderen/VSDS-LDES.git # Temporary build VSDS-LDES until there's an artifactory
WORKDIR /VSDS-LDES
RUN git checkout 69356992e012803b5fa4fb13a7bcfdf110ffcf55 # Use version 0.0.1-SNAPSHOT
RUN mvn clean install

RUN mkdir /ldes-server
WORKDIR /ldes-server
COPY . /ldes-server
RUN mvn clean install -DskipFormatCode=true

FROM openjdk:18-ea-alpine
COPY --from=builder /ldes-server/ldes-server-application/target/ldes-server-application.jar ./
RUN mkdir plugins
CMD ["java", "-cp", "ldes-server-application.jar", "-Dloader.path=plugins/", "org.springframework.boot.loader.PropertiesLauncher"]
