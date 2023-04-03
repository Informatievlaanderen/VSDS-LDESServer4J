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
FROM amazoncorretto:17-al2023-jdk
COPY --from=app-stage ldes-server-application/target/ldes-server-application.jar ./
COPY --from=app-stage ldes-server-infra-mongo/mongo-repository/target/mongo-repository-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-mongo/mongock-config/target/mongock-config-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-mongo/mongock-changeset-1/target/mongock-changeset-1-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-mongo/mongock-changeset-2/target/mongock-changeset-2-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-port-ingestion-rest/target/ldes-server-port-ingestion-rest-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-port-fetch-rest/target/ldes-server-port-fetch-rest-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-port-admin-rest/target/ldes-server-port-admin-rest-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-geospatial/target/ldes-fragmentisers-geospatial-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-timebased/target/ldes-fragmentisers-timebased-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-substring/target/ldes-fragmentisers-substring-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-pagination/target/ldes-fragmentisers-pagination-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-queues/ldes-queue-none/target/ldes-queue-none-jar-with-dependencies.jar ./lib/


CMD ["java", "-cp", "ldes-server-application.jar", "-Dloader.path=lib/", "org.springframework.boot.loader.PropertiesLauncher"]
