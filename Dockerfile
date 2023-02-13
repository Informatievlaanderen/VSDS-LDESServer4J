# syntax=docker/dockerfile:1

#
# INSTALL MAVEN DEPENDENCIES
#
FROM maven:3.8.5-openjdk-18 AS builder

# MAVEN: application
FROM builder as app-stage
COPY . /
RUN mvn net.revelc.code.formatter:formatter-maven-plugin:format install -DskipTests

#
# RUN THE APPLICATION
#
FROM openjdk:18-ea-bullseye
RUN apt-get update & apt-get upgrade

COPY --from=app-stage ldes-server-application/target/ldes-server-application.jar ./
COPY --from=app-stage ldes-server-infra-mongo/target/ldes-server-infra-mongo-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-port-ingestion-rest/target/ldes-server-port-ingestion-rest-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-port-fetch-rest/target/ldes-server-port-fetch-rest-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-geospatial/target/ldes-fragmentisers-geospatial-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-timebased/target/ldes-fragmentisers-timebased-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-substring/target/ldes-fragmentisers-substring-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-pagination/target/ldes-fragmentisers-pagination-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-queues/ldes-queue-none/target/ldes-queue-none-jar-with-dependencies.jar ./lib/

RUN useradd -u 2000 ldes
USER ldes

CMD ["java", "-cp", "ldes-server-application.jar", "-Dloader.path=lib/", "org.springframework.boot.loader.PropertiesLauncher"]
