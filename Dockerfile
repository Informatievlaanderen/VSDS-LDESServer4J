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

COPY --from=app-stage ldes-server-infra-mongo/mongo-repository/target/mongo-repository-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-mongo/mongock-changesets/target/mongock-changesets-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-mongo/mongo-ingest-repository/target/mongo-ingest-repository-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-mongo/mongo-fetch-repository/target/mongo-fetch-repository-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-mongo/mongo-retention-repository/target/mongo-retention-repository-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-mongo/mongo-fragmentation-repository/target/mongo-fragmentation-repository-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-mongo/mongo-admin-repository/target/mongo-admin-repository-jar-with-dependencies.jar ./lib/

COPY --from=app-stage ldes-server-port-ingest-rest/target/ldes-server-port-ingest-rest-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-port-ingest/target/ldes-server-port-ingest-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-port-fetch/target/ldes-server-port-fetch-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-port-fetch-rest/target/ldes-server-port-fetch-rest-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-admin/target/ldes-server-admin-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-common/target/ldes-fragmentisers-common-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-geospatial/target/ldes-fragmentisers-geospatial-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-timebased/target/ldes-fragmentisers-timebased-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-timebased-hierarchical/target/ldes-fragmentisers-timebased-hierarchical-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-substring/target/ldes-fragmentisers-substring-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-pagination/target/ldes-fragmentisers-pagination-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-retention/target/ldes-server-retention-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-compaction/target/ldes-server-compaction-jar-with-dependencies.jar ./lib/


RUN useradd -u 2000 ldes
USER ldes

CMD ["java", "-cp", "ldes-server-application.jar", "-Dloader.path=lib/", "org.springframework.boot.loader.PropertiesLauncher"]
