# syntax=docker/dockerfile:1

FROM amazoncorretto:21-alpine-jdk

COPY ./ldes-server-application/target/ldes-server-application.jar ./

# Postgres Implementations
COPY ./ldes-server-infra-postgres/postgres-pagination-repository/target/postgres-pagination-repository-jar-with-dependencies.jar ./lib/
COPY ./ldes-server-infra-postgres/postgres-maintenance-repository/target/postgres-maintenance-repository-jar-with-dependencies.jar ./lib/
COPY ./ldes-server-infra-postgres/postgres-fetch-repository/target/postgres-fetch-repository-jar-with-dependencies.jar ./lib/
COPY ./ldes-server-infra-postgres/postgres-ingest-repository/target/postgres-ingest-repository-jar-with-dependencies.jar ./lib/

# Interfaces
COPY ./ldes-server-ingest/ldes-server-ingest-rest/target/ldes-server-ingest-rest-jar-with-dependencies.jar ./lib/
COPY ./ldes-server-fetch/ldes-server-fetch-rest/target/ldes-server-fetch-rest-jar-with-dependencies.jar ./lib/
COPY ./ldes-server-admin/ldes-server-admin-rest/target/ldes-server-admin-rest-jar-with-dependencies.jar ./lib/

COPY ./ldes-server-ingest/ldes-server-ingest-kafka/target/ldes-server-ingest-kafka-jar-with-dependencies.jar ./lib/

# Plugin Fragmentations
COPY ./ldes-server-fragmentation/ldes-server-fragmentation-geospatial/target/ldes-server-fragmentation-geospatial-jar-with-dependencies.jar ./lib/
COPY ./ldes-server-fragmentation/ldes-server-fragmentation-timebased-hierarchical/target/ldes-server-fragmentation-timebased-hierarchical-jar-with-dependencies.jar ./lib/
COPY ./ldes-server-fragmentation/ldes-server-fragmentation-reference/target/ldes-server-fragmentation-reference-jar-with-dependencies.jar ./lib/

## Dependency for pyroscope
RUN apk --no-cache add libstdc++

RUN adduser -D -u 2000 ldes
USER ldes

CMD ["java", "-cp", "ldes-server-application.jar", "-Dloader.path=lib/", "org.springframework.boot.loader.PropertiesLauncher"]
