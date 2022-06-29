# syntax=docker/dockerfile:1

#
# INSTALL MAVEN DEPENDENCIES
#
FROM maven:3.8.5-openjdk-18 AS builder

# MAVEN: application
FROM builder as app-stage
COPY . /
RUN mvn install

#
# RUN THE APPLICATION
#
FROM openjdk:18-ea-alpine

COPY --from=app-stage ldes-server-application/target/ldes-server-application.jar ./
COPY --from=app-stage ldes-server-infra-mongo/target/ldes-server-infra-mongo-jar-with-dependencies.jar ./plugins/
COPY --from=app-stage ldes-server-port-ingestion-rest/target/ldes-server-port-ingestion-rest-jar-with-dependencies.jar ./plugins/
COPY --from=app-stage ldes-server-port-publication-rest/target/ldes-server-port-publication-rest-jar-with-dependencies.jar ./plugins/

CMD ["java", "-cp", "ldes-server-application.jar", "-Dloader.path=plugins/", "org.springframework.boot.loader.PropertiesLauncher"]
