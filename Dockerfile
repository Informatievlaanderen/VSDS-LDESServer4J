# syntax=docker/dockerfile:1

#
# INSTALL MAVEN DEPENDENCIES
#
FROM maven:3.9.6-amazoncorretto-21 AS builder

# MAVEN: application
FROM builder AS app-stage
COPY . /
RUN mvn install -DskipTests

#
# RUN THE APPLICATION
#
FROM amazoncorretto:21-alpine-jdk

COPY --from=app-stage ldes-server-application/target/ldes-server-application.jar ./

COPY --from=app-stage ldes-server-infra-postgres/postgres-ingest-repository/target/postgres-ingest-repository-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-postgres/postgres-fetch-repository/target/postgres-fetch-repository-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-postgres/postgres-retention-repository/target/postgres-retention-repository-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-postgres/postgres-fragmentation-repository/target/postgres-fragmentation-repository-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-postgres/postgres-pagination-repository/target/postgres-pagination-repository-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-postgres/postgres-admin-repository/target/postgres-admin-repository-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-infra-postgres/postgres-liquibase/target/postgres-liquibase-jar-with-dependencies.jar ./lib/

COPY --from=app-stage ldes-server-port-ingest-rest/target/ldes-server-port-ingest-rest-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-port-ingest/target/ldes-server-port-ingest-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-port-fetch/target/ldes-server-port-fetch-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-port-fetch-rest/target/ldes-server-port-fetch-rest-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-admin/target/ldes-server-admin-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-common/target/ldes-fragmentisers-common-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-geospatial/target/ldes-fragmentisers-geospatial-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-timebased-hierarchical/target/ldes-fragmentisers-timebased-hierarchical-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-fragmentisers/ldes-fragmentisers-reference/target/ldes-fragmentisers-reference-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-pagination/target/ldes-server-pagination-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-retention/target/ldes-server-retention-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-compaction/target/ldes-server-compaction-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldes-server-instrumentation/target/ldes-server-instrumentation-jar-with-dependencies.jar ./lib/

## Dependency for pyroscope
RUN apk --no-cache add libstdc++

RUN adduser -D -u 2000 ldes
USER ldes

CMD ["java", "-cp", "ldes-server-application.jar", "-Dloader.path=lib/", "org.springframework.boot.loader.PropertiesLauncher"]
