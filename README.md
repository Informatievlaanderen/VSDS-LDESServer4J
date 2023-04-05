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
                * [Example Views Configuration](#example-views-configuration)
                * [Example Retention](#example-retention)
                * [Example Timebased Fragmentation](#example-timebased-fragmentation)
                * [Example Geospatial Fragmentation](#example-geospatial-fragmentation)
                * [Example Substring Fragmentation](#example-substring-fragmentation)
                * [Example Pagination](#example-pagination)
                * [Example Serving Static Content](#example-serving-static-content)
                * [Example Serving DCAT Metadata](#example-serving-dcat-metadata)
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

## Set-up of the LDES Server

The current implementation consists of 6 modules:

- `ldes-server-application` which starts the spring boot application
- `ldes-server-domain` which contains the domain logic of the ldes server
- `ldes-server-infra-mongo` which allows to store ldes members and fragments in a mongoDB
- `ldes-server-port-ingestion-rest` which allows to ingest ldes members via HTTP
- `ldes-server-port-fetch-rest` which allows to retrieve fragments via HTTP
- `ldes-fragmentisers` which support different types of fragmentations

The modules `ldes-server-infra-mongo`, `ldes-server-port-ingestion-rest` and `ldes-server-port-fetch-rest` are built so
that they can be replaced by other implementations without the need for code changes in ldes-server-domain.

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
mvn spring-boot:run -P{fragmentation-timebased,http-fetch,http-ingest,queue-none,storage-mongo}
```

To enrich the server, certain Maven profiles can be activated:

#### Profiles

| Profile Group                        | Profile Name               | Description                                                     | Parameters                                                                  | Further Info                                                                                                                        |
|--------------------------------------|----------------------------|-----------------------------------------------------------------|-----------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| **HTTP Endpoints (Fetch/Ingestion)** | http-ingest                | Enables a HTTP endpoint for to insert LDES members.             | [HTTP configuration](#example-http-ingest-fetch-configuration)              | Endpoint:<br><br>- URL: /{ldes.collection-name}<br>- Request type: POST<br>- Accept: "application/n-quads", "application/n-triples" |
| **HTTP Endpoints (Fetch/Ingestion)** | http-fetch                 | Enables a HTTP endpoint to retrieve LDES fragments              | [Example Views Configuration](#example-views-configuration)                 | Endpoint:<br>- URL: /{views.name}<br><br>- Request type: GET<br>- Accept: "application/n-quads", "application/ld+json"              |
| **Storage**                          | storage-mongo              | Allows the LDES server to read and write from a mongo database. | [Mongo configuration](#example-mongo-configuration)                         |                                                                                                                                     |
| **Timebased Fragmentation**          | fragmentation-timebased    | Supports timebased fragmentation.                               | [Timebased fragmentation configuration](#example-timebased-fragmentation)   |                                                                                                                                     |
| **Geospatial Fragmentation**         | fragmentation-geospatial   | Supports geospatial fragmentation.                              | [Geospatial fragmentation configuration](#example-geospatial-fragmentation) |                                                                                                                                     |
| **Substring Fragmentation**          | fragmentation-substring    | Supports substring fragmentation.                               | [Geospatial fragmentation configuration](#example-geospatial-fragmentation) |                                                                                                                                     |
| **Substring Fragmentation**          | fragmentation-substring    | Supports substring fragmentation.                               | [Geospatial fragmentation configuration](#example-geospatial-fragmentation) |                                                                                                                                     |
| **Pagination Fragmentation**         | fragmentation-pagination   | Supports pagination.                                            | [Pagination configuration](#example-pagination)                             | The pagenumbers start with pagenumber 1                                                                                             |
| **Ldes-queues**                      | queue-none                 | Members are fragmented immediately.                             | N/A activating the profile is enough                                        |                                                                                                                                     |
| **Ldes-queues**                      | queue-in-memory            | Members are queued in memory before fragmentation.              | N/A activating the profile is enough                                        |                                                                                                                                     |

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
  ldes:
    collection-name: { short name of the collection, cannot contain characters that are used for url interpretation, e.g. '$', '=' or '&' }
    host-name: { hostname of LDES Server }
    member-type: { Defines which syntax type is used to define the member id e.g. "https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder" }
    timestamp-path: { SHACL property path to the timestamp when the version object entered the event stream. }
    version-of: { SHACL property path to the non-versioned identifier of the entity. }
    validation:
      shape: { URI to defined shape }
      enabled: { Enables/Disables shacl validation on ingested members }
  rest:
    max-age: { time in seconds that a mutable fragment can be considered up-to-date, default when omitted: 60 }
    max-age-immutable: { time in seconds that an immutable fragment should not be refreshed, default when omitted: 604800 }
  ```

##### Example Mongo Configuration

  ```yaml
  spring.data.mongodb:
    uri: mongodb://{docker-hostname}:{port}
    database: { database name }
  ```
##### Example Views Configuration

  ```yaml
    views:
      - name: {name of the view}
        fragmentations:
          - name: {type of fragmentation, currently "timebased" and "geospatial" supported}
            config:
              {Map of fragmentation properties}
  ```

An example of a view configuration with two view is shown below

  ```yaml
  views:
    - name: "firstView"
      fragmentations:
        - name: "geospatial"
          config:
            maxZoomLevel: 15
            fragmenterProperty: "http://www.opengis.net/ont/geosparql#asWKT"
        - name: "timebased"
          config:
            memberLimit: 5
    - name: "secondView"
      fragmentations:
        - name: "timebased"
          config:
            memberLimit: 3
  ```

##### Example Retention

To reduce storage fill up, it is possible to set a retention policy per view.
As of now, there is only a timebased retention possible which can be configured with a [ISO 8601 duration](https://tc39.es/proposal-temporal/docs/duration.html)

  ```yaml
  views:
    - name: "firstView"
      retentionPolicies:
        - name: "timebased"
          config:
            duration: "PT5M"
  ```

##### Example Timebased Fragmentation

  ```yaml
  name: "timebased"
  config:
    memberLimit: { member limit > 0 }
  ```

##### Example Geospatial Fragmentation

  ```yaml
  name: "geospatial"
  config:
    maxZoomLevel: { Required zoom level }
    fragmenterProperty: { Defines which property will be used for bucketizing }
  ```

##### Example Substring Fragmentation

  ```yaml
  name: "substring"
  config:
    fragmenterProperty: { Defines which property will be used for bucketizing }
    memberLimit: { member limit > 0 }
  ```

##### Example Pagination

  ```yaml
  name: "pagination"
  config:
    memberLimit: { member limit > 0 }
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

  ```yaml
ldes:
  dcat: { path of file or url containing DCAT metadata, e.g. "dcat/metadata.ttl"  }
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

Change the `env_file` to `config.env` or `config.local.env` in [compose.yml](compose.yml) according to
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
- ldes-server-port-ingestion-rest
- ldes-server-port-publication-rest
- ldes-fragmentisers-timebased
- ldes-fragmentisers-geospatial

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