# LDES Server

The Linked Data Event Stream (LDES) server is a configurable component that can be used to ingest, store, transform
and (re-)publish an [LDES](https://semiceu.github.io/LinkedDataEventStreams/).
The LDES server was built in the context of
the [VSDS project](https://vlaamseoverheid.atlassian.net/wiki/spaces/VSDSSTART/overview) in order to easily exchange
open data.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Informatievlaanderen_VSDS-LDESServer4J&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Informatievlaanderen_VSDS-LDESServer4J)

## Table Of Contents

- [LDES Server](#ldes-server)
    * [Table Of Contents](#table-of-contents)
    * [Set-up of the LDES Server](#set-up-of-the-ldes-server)
    * [How To Run](#how-to-run)
        + [Locally](#locally)
            - [Maven](#maven)
            - [Profiles](#profiles)
            - [Application Configuration](#application-configuration)
                * [Example HTTP Ingest-Fetch Configuration](#example-http-ingest-fetch-configuration)
                * [Example Mongo Configuration](#example-mongo-configuration)
                * [Example Swagger Configuration](#example-swagger-configuration)
                * [Example Views Configuration](#example-views-configuration)
                * [Example Retention](#example-retention)
                * [Timebased retention](#timebased-retention)
                * [Example versionbased retention](#example-versionbased-retention)
                * [Example point in time retention](#example-point-in-time-retention)
                * [Fragmentation](#fragmentation)
                * [Example Timebased Fragmentation](#example-timebased-fragmentation)
                * [Example Geospatial Fragmentation](#example-geospatial-fragmentation)
                * [Example Substring Fragmentation](#example-substring-fragmentation)
                * [Example Pagination](#example-pagination)
                * [Example Hierarchical Timebased Fragmentation](#example-hierarchical-timebased-fragmentation)
                * [Example Serving Static Content](#example-serving-static-content)
                * [Example Serving DCAT Metadata](#example-serving-dcat-metadata)
                * [Example Compaction](#example-compaction)
        + [Docker Setup](#docker-setup)
            - [Docker-compose](#docker-compose)
            - [The Config Files](#the-config-files)
            - [The docker compose File](#the-docker-compose-file)
            - [Starting the Dockerized Application](#starting-the-dockerized-application)
    * [Developer Information](#developer-information)
        + [How To Build](#how-to-build)
        + [How To Test and View Coverage](#how-to-test-and-view-coverage)
            - [Unit and Integration Tests](#unit-and-integration-tests)
            - [Only Unit Tests](#only-unit-tests)
            - [Only Integration Tests](#only-integration-tests)
            - [Auto-Configurable Modules](#auto-configurable-modules)
        + [Tracing and Metrics](#tracing-and-metrics)
            - [Local Tracing and Metrics](#local-tracing-and-metrics)
            - [Using Docker](#using-docker)
        + [Health and Info](#health-and-info)
            - [Local Health and Info](#local-health-and-info)
            - [Docker](#docker)
        + [Logging](#logging)
            - [Logging configuration](#logging-configuration)

## Set-up of the LDES Server

The current implementation consists of the following modules:

- `ldes-server-application` which starts the spring boot application
- `ldes-server-domain` which contains the domain logic of the ldes server
- `ldes-server-infra-mongo` which allows to store ldes members and fragments in a mongoDB
- `ldes-server-port-ingest` which allows to ingest ldes members
- `ldes-server-port-fetch-rest` which allows to retrieve fragments via HTTP
- `ldes-fragmentisers` which support different types of fragmentations

The modules `ldes-server-infra-mongo` is built so
that it can be replaced by other implementations without the need for code changes in ldes-server-domain.

## How To Run

We'll show you how to set up your own LDES server both locally and via Docker using a mongo db for storage and a rest
endpoint for ingestion and fetch.
Afterwards, you can change storage, ingestion and fetch options by plugging in other components.

### Locally

#### Maven

To locally run the LDES server in Maven, move to the `ldes-server-application` directory and run the Spring Boot
application.

This can be done as follows:
> **_NOTE:_**  Due to an authorisation issue in GitHub packages, the easiest way is to first build the project using the
> following command:
> ```mvn
>  mvn install
> ```

```shell
cd ldes-server-application
```

```mvn
mvn spring-boot:run -P{profiles (comma separated with no spaces) }
```

for example:
```mvn
mvn spring-boot:run -P{fragmentation-pagination,http-fetch,http-ingest,queue-none,storage-mongo}
```

To enrich the server, certain Maven profiles can be activated:

#### Profiles

| Profile Group                            | Profile Name                         | Description                                                                     | Parameters                                                                             | Further Info                                                                                                                        |
|------------------------------------------|--------------------------------------|---------------------------------------------------------------------------------|----------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| **HTTP Endpoints (Fetch/Ingestion)**     | http-ingest                          | Enables a HTTP endpoint to insert LDES members.                                 | [HTTP configuration](#example-http-ingest-fetch-configuration)                         | Endpoint:<br><br>- URL: /{ldes.collection-name}<br>- Request type: POST<br>- Accept: "application/n-quads", "application/n-triples" |
| **HTTP Endpoints (Fetch/Ingestion)**     | http-fetch                           | Enables a HTTP endpoint to retrieve LDES fragments                              | [Example Views Configuration](#example-views-configuration)                            | Endpoint:<br>- URL: /{views.name}<br><br>- Request type: GET<br>- Accept: "application/n-quads", "application/ld+json"              |
| **Storage**                              | storage-mongo                        | Allows the LDES server to read and write from a mongo database.                 | [Mongo configuration](#example-mongo-configuration)                                    |                                                                                                                                     |
| **Timebased Fragmentation[DEPRECATED]**  | fragmentation-timebased              | Supports timebased fragmentation.                                               | [Timebased fragmentation configuration](#example-timebased-fragmentation)              |                                                                                                                                     |
| **Geospatial Fragmentation**             | fragmentation-geospatial             | Supports geospatial fragmentation.                                              | [Geospatial fragmentation configuration](#example-geospatial-fragmentation)            |                                                                                                                                     |
| **Substring Fragmentation**              | fragmentation-substring              | Supports substring fragmentation.                                               | [Substring fragmentation configuration](#example-substring-fragmentation)              |                                                                                                                                     |
| **Pagination Fragmentation**             | fragmentation-pagination             | Supports pagination.                                                            | [Pagination configuration](#example-pagination)                                        | The pagenumbers start with pagenumber 1                                                                                             |
| **Hierarchical Timebased Fragmentation** | fragmentation-timebased-hierarchical | Supports hierarchical timebased fragmentation.                                  | [Timebased fragmentation configuration](#example-hierarchical-timebased-fragmentation) |                                                                                                                                     |
| **Ldes-queues**                          | queue-none                           | Members are fragmented immediately.                                             | N/A activating the profile is enough                                                   |                                                                                                                                     |
| **Ldes-queues**                          | queue-in-memory                      | Members are queued in memory before fragmentation.                              | N/A activating the profile is enough                                                   |                                                                                                                                     |
| **HTTP Endpoints (Admin)**               | http-admin                           | Enables HTTP endpoints. These will be used later to configure different streams |                                                                                        |                                                                                                                                     |


The main functionalities of the server are ingesting and fetching, these profiles depend on other supporting profiles to function properly:
- http-ingest: requires at least one queue, one fragmentation and one storage profile.
- http-fetch: requires at least one storage profile

#### Application Configuration

Below are properties that are needed when applying certain profiles.
These need to be added in the `application.yml` file in `ldes-server-application/src/main/resources`. (If the file does not exist, create it)

A minimal working example of the application.yml can be found [here](ldes-server-application/examples/minimal-config-application.yml). This works for both fetching and ingesting. 

The server allows configurable fragment refresh times with the max-age and max-age-immutable options. These values will be sent with the Cache-Control header in HTTP responses.

##### Example HTTP Ingest-Fetch Configuration


  ```yaml
  server.port: { http-port }
  host-name: { hostname of LDES Server }
  rest:
    max-age: { time in seconds that a mutable fragment can be considered up-to-date, default when omitted: 60 }
    max-age-immutable: { time in seconds that an immutable fragment should not be refreshed, default when omitted: 604800 }
  ```

The server can host one or more collections.
These collection are to be added one by one on the following POST endpoint.
  ```
  {server_url}/admin/api/v1/eventstreams  
  ```
The collection must be passed in RDF in either turtle, n-quads or json-ld format.
An example can be found [here](ldes-server-admin/README.md#ldes-event-stream)

A detailed explanation on how to add or remove a collection can be found [here](ldes-server-admin/README.md)

##### Example Mongo Configuration

  ```yaml
  spring.data.mongodb:
    uri: mongodb://{docker-hostname}:{port}
    database: { database name }
    auto-index-creation: true
  ```

The `auto-index-creation` enables the server to automatically create indices in mongodb.
If this property is not enabled, you have to manage the indices manually. This can have a significant impact on performance.

Note that the database schema may evolve between releases. To update the schema Mongock changesets have been created and can be applied. 
For more see the individual readme files of the changesets: [mongock-changeset](ldes-server-infra-mongo/mongock-changesets/src/main/java/be/vlaanderen/informatievlaanderen/ldes/server/infra/mongo/mongock/)

##### Example Swagger Configuration

By configuring swagger, an overview of all available endpoints can be viewed.
The path for this overview when using the following example is ```{host-name}/v1/swagger```
  ```yaml
  springdoc:
    swagger-ui:
      path: /v1/swagger
  ```

##### Example Views Configuration

A collection can have a default view configured (paginated with 100 members per page).
This can be added by adding the following statement to the collection rdf.
  ```
  custom:hasDefaultView "true"^^xsd:boolean ;
  ```

Additional views can be added one by one on the following POST endpoint.
  ```
  {server_url}/admin/api/v1/eventstreams/{collection_name}/views
  ```
The view must be passed in RDF in either turtle, n-quads or json-ld format.
An example can be found [here](ldes-server-admin/README.md#ldes-view)

A detailed explanation on how to add or remove a view can be found [here](ldes-server-admin/README.md)



##### Example Retention

To reduce storage fill up, it is possible to set a retention policy per view.
A retention policy has to be added together with its view.
Currently, there are 3 possible retention policies each with a different type and parameter.

| Retention         | RDF syntax type                           | Description                                                 | Parameters                  |
|-------------------|-------------------------------------------|-------------------------------------------------------------|-----------------------------|
| **Timebased**     | https://w3id.org/ldes#DurationAgoPolicy   | Removes members older than a given period                   | tree:value (duration)       |
| **versionbased**  | https://w3id.org/ldes#LatestVersionSubset | Only retains the x most recent members of each state object | tree:amount (integer > 0)   |
| **point in time** | https://w3id.org/ldes#PointInTimePolicy   | Only retains the members made after a given point in time   | ldes:pointInTime (dateTime) |

##### Timebased retention

  ```ttl
  @prefix ldes: <https://w3id.org/ldes#> .
  @prefix tree: <https://w3id.org/tree#>.

  <view1> a tree:Node ;
    tree:viewDescription [
      a tree:ViewDescription ;
      ldes:retentionPolicy [
        a ldes:DurationAgoPolicy ;
        tree:value "PT10M"^^<http://www.w3.org/2001/XMLSchema#duration> ;
      ] ;
    ] .
  ```

##### Example versionbased retention

  ```ttl
  @prefix ldes: <https://w3id.org/ldes#> .
  @prefix tree: <https://w3id.org/tree#>.

  <view1> a tree:Node ;
    tree:viewDescription [
      a tree:ViewDescription ;
      ldes:retentionPolicy [
        a ldes:LatestVersionSubset ;
        tree:amount 2 ;
      ] ;
    ] .
  ```

##### Example point in time retention

  ```ttl
  @prefix ldes: <https://w3id.org/ldes#> .
  @prefix tree: <https://w3id.org/tree#>.

  <view1> a tree:Node ;
    tree:viewDescription [
      a tree:ViewDescription ;
      ldes:retentionPolicy [
        a ldes:PointInTimePolicy ;
        <https://w3id.org/ldes#pointInTime>
          "2023-04-12T00:00:00"^^<http://www.w3.org/2001/XMLSchema#dateTime> ;
      ] ;
    ] .
  ```

##### Fragmentation

To configure a view with a certain fragmentation,
a fragmentation strategy has to be added when creating the view. The final step in the fragmentation strategy is automatically pagination. Per view the size of the pages can be configured as follows:

```ttl
  @prefix tree: <https://w3id.org/tree#>.
  parcels:pagination tree:viewDescription [
    tree:pageSize "100"^^<http://www.w3.org/2001/XMLSchema#int> ;
    tree:fragmentationStrategy () ;
  ] .
```


##### Example Timebased Fragmentation

This fragmentation is DEPRECATED, more information can be found [here](ldes-fragmentisers/ldes-fragmentisers-timebased/README.MD)

##### Example Geospatial Fragmentation

Full documentation for geospatial fragmentation can be found [here](ldes-fragmentisers/ldes-fragmentisers-geospatial/README.MD)

  ```ttl
  @prefix ldes: <https://w3id.org/ldes#> .
  @prefix tree: <https://w3id.org/tree#>.

  <view1> a tree:Node ;
    tree:viewDescription [
      a tree:ViewDescription ;
      tree:fragmentationStrategy ([
        a tree:GeospatialFragmentation ;
        tree:maxZoom "15" ;
        tree:fragmentationPath <http://www.opengis.net/ont/geosparql#asWKT> ;
      ]) ;
    ] .
  
  
  
  ```

##### Example Substring Fragmentation

Full documentation for substring fragmentation can be found [here](ldes-fragmentisers/ldes-fragmentisers-substring/README.MD)

  ```ttl
  @prefix prov: <http://www.w3.org/ns/prov#> .
  @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
  @prefix ldes: <https://w3id.org/ldes#> .
  @prefix tree: <https://w3id.org/tree#>.

  <view1> a tree:Node ;
    tree:viewDescription [
      a tree:ViewDescription ;
      tree:fragmentationStrategy ([
        a tree:SubstringFragmentation ;
        tree:memberLimit "10" ;
        tree:fragmentationPath <https://data.vlaanderen.be/ns/adres#volledigAdres> ;
      ]) ;
    ] .
  ```

##### Example Pagination

Full documentation for pagination fragmentation can be found [here](ldes-fragmentisers/ldes-fragmentisers-pagination/README.MD)

  ```ttl
  @prefix ldes: <https://w3id.org/ldes#> .
  @prefix tree: <https://w3id.org/tree#>.

  <view1> a tree:Node ;
    tree:viewDescription [
        a tree:ViewDescription ;
        tree:fragmentationStrategy ([
          a tree:PaginationFragmentation ;
          tree:memberLimit 10 ;
        ]) ;
  ] .
  ```

##### Example Hierarchical Timebased Fragmentation

Full documentation for hierarchical timebased fragmentation can be found [here](ldes-fragmentisers/ldes-fragmentisers-timebased-hierarchical/README.MD)

  ```ttl
  @prefix tree: <https://w3id.org/tree#> .
  
  tree:fragmentationStrategy ([
        a tree:HierarchicalTimeBasedFragmentation ;
        tree:maxGranularity "day" ;
        tree:fragmentationPath <http://www.w3.org/ns/prov#generatedAtTime> ;
    ]) .
  ```

##### Example Serving Static Content

  ```yaml
spring:
  mvc:
    static-path-pattern: { pattern used in url for static content, e.g. /content/** for serving on http://localhost:8080/content/ }
  web:
    resources:
      static-locations: { source folder of static content, e.g. file:/opt/files }
  ```

##### Example Serving DCAT Metadata

Supported file formats: .ttl, .rdf, .nq and .jsonld
Templates for configuring the DCAT metadata can be found [here](templates/dcat)
A detailed explanation on how to manage and retrieve the DCAT metadata can be found [here](ldes-server-admin/README.md#dcat-endpoints)
or on the [swagger endpoint](#example-swagger-configuration) if it is configured.

##### Example Compaction

Compaction is a process that allows the server to merge immutable fragments that are underutilized (i.e. there are fewer members in the fragment than indicated in the `pageSize` of the view).
Merging the fragments will result in a new fragment and the members and relations of the compacted fragments will be "copied" to the new fragment.
This process runs entirely in the background. By default, the fragments that have been compacted will remain available for 7 days, `PD7`, but it can be configured differently. After that period they will be deleted.

```yaml
ldes-server:
  compaction-duration: "PT1M"
```

### Docker Setup

#### Docker-compose

There are 2 files where you can configure the dockerized application:

- [The config files](#the-config-files)
- [The docker compose file](#the-docker-compose-file)

#### The Config Files

Runtime settings can be defined in the configuration files. Use [config.env](docker-compose/config.env) for a public
setup and be sure not to commit this file, as it contains secrets. For a local setup,
use [config.local.env](docker-compose/config.local.env).

#### The docker compose File

Change the `env_file` to `config.env` or `config.local.env` in [compose.yml](docker-compose.yml) according to
your needs.

#### Starting the Dockerized Application

Run the following commands to start the containers:

```bash
COMPOSE_DOCKER_CLI_BUILD=1 docker-compose build
docker-compose up
```

> **Note**: Using Docker Desktop might fail because of incorrect environment variable interpretation.
> 
> ```unexpected character "-" in variable name near ...```


## Developer Information

### How To Build

For compilation of the source code, execute the following command

```
mvn clean compile
```

### How To Test and View Coverage

Below the three options to run tests (only unit test, only integration tests and both unit and integration tests) are
listed.
To view the coverage of each option it's sufficient to add the option `-Pcoverage` after the command.
This generates a file in the `target` folder called `jacoco.exec`. This file reports the code coverage.
The combined coverage can also be seen via SonarCloud.

#### Unit and Integration Tests

For running all the tests of the project, execute the following command

```
mvn clean verify
```

#### Only Unit Tests

For running the unit tests of the project, execute the following command

```
mvn clean verify -Dintegrationtestskip=true
```

#### Only Integration Tests

For running the integration tests of the project, execute the following command

```
mvn clean verify -Dunittestskip=true
```

#### Auto-Configurable Modules

- ldes-server-infra-mongo
- ldes-server-port-ingest
- ldes-server-port-publication-rest
- ldes-fragmentisers-timebased
- ldes-fragmentisers-geospatial
- ldes-fragmentisers-pagination
- ldes-fragmentisers-substring

### Tracing and Metrics

Additionally, it is possible to keep track of metrics and tracings of the LDES Server.
This will be done through a Zipkin exporter for traces and a Prometheus endpoint for Metrics.

The exposed metrics can be found at `/actuator/metrics`.

Both traces and metrics are based on [OpenTelemetry standard](https://opentelemetry.io/docs/concepts/what-is-opentelemetry/)


To achieve this, the following properties are expected

#### Local Tracing and Metrics

```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  zipkin:
    tracing:
      endpoint: "zipkin endpoint of collector"
  endpoints:
    web:
      exposure:
        include: 
          - prometheus
```

The export of traces can be disabled with the following parameter:

```yaml
management:
  tracing:
    enabled: false
  ```

#### Using Docker

```
SPRING_SLEUTH_OTEL_EXPORTER_JAEGER_ENDPOINT="endpoint of collector"
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE="prometheus"
MANAGEMENT_TRACING_SAMPLING_PROBABILITY="1.0"
MANAGEMENT_ZIPKIN_TRACING_ENDPOINT="zipkin endpoint of collector"
```
The export of traces can be disabled with the following parameter:

```
MANAGEMENT_TRACING_ENABLED=false
```

### Health and Info

To allow more visibility for the application, it is possible to enable a health and info endpoint.

This health endpoint provides a basic JSON output that can be found at `/actuator/health` that provides a summary of the status of all needed services.

An additional info endpoint is also available which shows which version of the application running and its deployment date.

#### Local Health and Info

The following config allows you to enable both the info and health endpoints.
```yaml
management:
  endpoints:
    web:
      exposure:
        include:
          - health
          - info
  health:
    defaults:
      enabled: false
    mongo:
      enabled: true
  endpoint:
    health:
      show-details: always
  ```

#### Docker

```
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE="health, info"
MANAGEMENT_HEALTH_DEFAULTS_ENABLED=false
MANAGEMENT_HEALTH_MONGO_ENABLED=true
MANAGEMENT_ENDPOINT_HEALTH_SHOW-DETAILS="always"
```

### Logging


The logging of this server is split over the different logging levels according to the following guidelines.

- TRACE: NONE
- DEBUG: Standard operations like: create fragment, ingest member, assign member to fragment
- INFO: NONE
- WARN: Potentially unintended operations like: Duplicate Member Ingest, ...
- ERROR: All Exceptions

#### Logging configuration

The following config allows you to output logging to the console. Further customization of the logging settings can be done using the logback properties.
```yaml
logging:
  pattern:
    console: "%d %-5level %logger : %msg%n"
  level:
    root: INFO
```

The following config enables and exposes the loggers endpoint.
```yaml
management:
  endpoint:
    loggers:
      enabled: true
  endpoints:
    web:
      exposure:
        include:
          - loggers
```

To change the logging level of the application at runtime, you can send the following POST request to the loggers endpoint.
Replace [LOGGING LEVEL] with the desired logging level from among: TRACE, DEBUG, INFO, WARN, ERROR.
```
curl -i -X POST -H 'Content-Type: application/json' -d '{"configuredLevel": "[LOGGING LEVEL]"}'
  http://localhost:8080/actuator/loggers/ROOT
```
