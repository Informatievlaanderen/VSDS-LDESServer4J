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
| **Pagination Fragmentation**             | fragmentation-pagination             | Supports pagination.                                                            | [Pagination configuration](#example-pagination)                                        | The pagenumbers start with pagenumber 1                                                                                             |
| **Hierarchical Timebased Fragmentation** | fragmentation-timebased-hierarchical | Supports hierarchical timebased fragmentation.                                  | [Timebased fragmentation configuration](#example-hierarchical-timebased-fragmentation) |                                                                                                                                     |
| **Ldes-queues**                          | queue-none                           | Members are fragmented immediately.                                             | N/A activating the profile is enough                                                   |                                                                                                                                     |
| **Ldes-queues**                          | queue-in-memory                      | Members are queued in memory before fragmentation.                              | N/A activating the profile is enough                                                   |                                                                                                                                     |
| **HTTP Endpoints (Admin)**               | http-admin                           | Enables HTTP endpoints. These will be used later to configure different streams |                                                                                        |                                                                                                                                     |
| **Instrumentation**                      | instrumentation                      | Enables pyroscope to collect data for instrumentation.                          | [Pyroscope configuration](#Pyroscope instrumentation)                                  |                                                                                                                                     |

The main functionalities of the server are ingesting and fetching, these profiles depend on other supporting profiles to
function properly:

- http-ingest: requires at least one queue, one fragmentation and one storage profile.
- http-fetch: requires at least one storage profile

#### Config

To run the server, some properties must be configured. The minimal config can be
found [here](ldes-server-application/examples/minimal-config-application.yml)

##### Overview server config

  ```yaml
ldes-server:
  host-name: "http://localhost:8080"
  use-relative-url: true
  ```

| Property         | Description                                                                          | Required | Default | Example               | Supported values    |
|:-----------------|:-------------------------------------------------------------------------------------|:---------|:--------|:----------------------|:--------------------|
| host-name        | The host name of the server, used as a prefix for resources hosted on the server.    | Yes      | N/A     | http://localhost:8080 | HTTP and HTTPS urls |
| use-relative-url | Determines if the resources hosted on the server are constructed with a relative URI | No       | false   | true, false           | true, false         |

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
A detailed explanation on how to manage and retrieve the DCAT metadata can be
found [here](ldes-server-admin/README.md#dcat-endpoints).

##### Relative urls

To enable relative urls on the server, set the following property:

  ```yaml
ldes-server:
  use-relative-url: true
  ```

When fetching any page using relative urls, any resource hosted on the server will have a URI relative to the requested
page.

> **Note**: When using relative urls, GET requests can not be performed with N-quads or triples as accept-type.
> This is because these types don't support resolving the relative URI's

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
- ldes-fragmentisers-timebased-hierarchical
- ldes-fragmentisers-geospatial
- ldes-fragmentisers-pagination
- ldes-fragmentisers-reference

### Tracing and Metrics

Additionally, it is possible to keep track of metrics and tracings of the LDES Server.
This will be done through a Zipkin exporter for traces and a Prometheus endpoint for Metrics.

The exposed metrics can be found at `/actuator/metrics`.

Both traces and metrics are based
on [OpenTelemetry standard](https://opentelemetry.io/docs/concepts/what-is-opentelemetry/)

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

#### Pyroscope instrumentation

To enable pyroscope, add the following to the application.yml file:

```yaml
pyroscope:
  agent:
    enabled: true
```

Note that this does not work when running the server locally on a Windows device.

The normal pyroscope properties can be
found [here](https://grafana.com/docs/pyroscope/latest/configure-client/language-sdks/java/#java-client-configuration-options)
These properties should be added to the env variables.

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

This health endpoint provides a basic JSON output that can be found at `/actuator/health` that provides a summary of the
status of all needed services.

An additional info endpoint is also available which shows which version of the application running and its deployment
date.

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
    dcat:
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
MANAGEMENT_HEALTH_DCAT_ENABLED=true
MANAGEMENT_ENDPOINT_HEALTH_SHOW-DETAILS="always"
```

#### Exposing of details

With the above config, where `management.endpoint.health.show-details=true`, all the details of the declared health
components are exposed. The details that should not be exposed, can be hidden by two ways.

1. Disabling unnecessary details \
   The details that should not be exposed, can be disabled. This can be achieved by disabling all the defaults and
   enabling the required details only, just like the above config, or by enabling the required details only.
   This can be done with the following property: `management.endpoints.<component-name>.enabled=<true/false>`

2. Declaring a group and include there all the desired details \
   With the following config, a group is created that exposes all its details of the components within this group. This
   ensures that other details that are not part of this group are not exposed.
    ```yaml
    management:
      endpoint:
        health:
          show-details: always
          group:
            dcat-validity:
              show-details: always
              include: dcat
    ```

### Logging

The logging of this server is split over the different logging levels according to the following guidelines.

- TRACE: NONE
- DEBUG: Standard operations like: create fragment, ingest member, assign member to fragment
- INFO: NONE
- WARN: Potentially unintended operations like: Duplicate Member Ingest, ...
- ERROR: All Exceptions

#### Logging configuration

The following config allows you to output logging to the console. The trace id and span id can be included in the
logging,
if enabled via the [tracing config](#tracing-and-metrics).
Further customization of the logging settings can be done using the logback properties. A use case for this can be
sending the logs to Loki for example.

```yaml
logging:
  pattern:
    console: "%5p [${spring.application.name:LDESServer4J},%X{traceId:-},%X{spanId:-}]"
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

To change the logging level of the application at runtime, you can send the following POST request to the loggers
endpoint.
Replace [LOGGING LEVEL] with the desired logging level from among: TRACE, DEBUG, INFO, WARN, ERROR.

```
curl -i -X POST -H 'Content-Type: application/json' -d '{"configuredLevel": "[LOGGING LEVEL]"}'
  http://localhost:8080/actuator/loggers/ROOT
```
