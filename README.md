# LDES Server

The Linked Data Event Stream (LDES) server is a configurable component that can be used to ingest, store, transform and (re-)publish an [LDES](https://semiceu.github.io/LinkedDataEventStreams/).
The LDES server was built in the context of the [VSDS project](https://vlaamseoverheid.atlassian.net/wiki/spaces/VSDSSTART/overview) in order to easily exchange open data.  

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
        * [Example Timebased Fragmentation](#example-timebased-fragmentation)
        * [Example Geospatial Fragmentation](#example-geospatial-fragmentation)
    + [Docker](#docker)
      - [Docker-compose](#docker-compose)
      - [The Config Files](#the-config-files)
      - [The docker-compose File](#the-docker-compose-file)
      - [Starting the Dockerized Application](#starting-the-dockerized-application)
  * [Developer Information](#developer-information)
    + [How To Build](#how-to-build)
    + [How To Test](#how-to-test)
      - [Unit and Integration Tests](#unit-and-integration-tests)
      - [Only Unit Tests](#only-unit-tests)
      - [Only Integration Tests](#only-integration-tests)
      - [Auto-Configurable Modules](#auto-configurable-modules)

## Set-up of the LDES Server

The current implementation consists of 6 modules:
- `ldes-server-application` which starts the spring boot application
- `ldes-server-domain` which contains the domain logic of the ldes server
- `ldes-server-infra-mongo` which allows to store ldes members and fragments in a mongoDB
- `ldes-server-port-ingestion-rest` which allows to ingest ldes members via HTTP
- `ldes-server-port-fetch-rest` which allows to retrieve fragments via HTTP
- `ldes-fragmentisers` which support different types of fragmentations

The modules `ldes-server-infra-mongo`, `ldes-server-port-ingestion-rest` and `ldes-server-port-fetch-rest` are built so that they can be replaced by other implementations without the need for code changes in ldes-server-domain.

## How To Run
We'll show you how to set up your own LDES server both locally and via Docker using a mongo db for storage and a rest endpoint for ingestion and fetch.
Afterwards, you can change storage, ingestion and fetch options by plugging in other components.

### Locally

#### Maven

To locally run the LDES server in Maven, move to the ldes-server-application directory and run the Spring Boot application.

This can be done as follows:
> **_NOTE:_**  Due to an authorisation issue in GitHub packages, the easiest way is to first build the project using the following command:
> ```mvn
>  mvn install
> ```

```shell
cd ldes-server-application
```
```mvn
mvn spring-boot:run -P {profiles (comma separated with no spaces) }
```

This will start an empty LDES server. To enrich this server, certain Maven profiles can be activated:

#### Profiles

| Profile Group                        | Profile Name             | Description                                                     | Parameters                                                                  | Further Info                                                                                                                        |
|--------------------------------------|--------------------------|-----------------------------------------------------------------|-----------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| **HTTP Endpoints (Fetch/Ingestion)** | http-ingest              | Enables a http endpoint for to insert LDES members.             | [HTTP configuration](#example-http-ingest-fetch-configuration)              | Endpoint:<br><br>- URL: /{ldes.collection-name}<br>- Request type: POST<br>- Accept: "application/n-quads", "application/n-triples" |
| **HTTP Endpoints (Fetch/Ingestion)** | http-fetch               | Enables a http endpoint to retrieve LDES fragments              | [HTTP configuration](#example-http-ingest-fetch-configuration)              | Endpoint:<br>- URL: /{ldes.collection-name}<br><br>- Request type: GET<br>- Accept: "application/n-quads", "application/ld+json"    |
| **Storage**                          | storage-mongo            | Allows the LDES server to read and write from a mongo database. | [Mongo configuration](#example-mongo-configuration)                         |                                                                                                                                     |
| **Timebased Fragmentation**          | fragmentation-timebased  | Supports timebased fragmentation.                               | [Timebased fragmentation configuration](#example-timebased-fragmentation)   |                                                                                                                                     |
| **Geospatial Fragmentation**         | fragmentation-geospatial | Supports geospatial fragmentation.                              | [Geospatial fragmentation configuration](#example-geospatial-fragmentation) |                                                                                                                                     |

#### Application Configuration
Below are properties that are needed when applying certain profiles. 
These need to be added in the `application.yml` file in `ldes-server-application/src/main/resources`. (If the file does not exist, create it)

##### Example HTTP Ingest-Fetch Configuration
  ```yaml
  server.port: {http-port}
  ldes:
    collection-name: {short name of the collection}
    host-name: {endpoint of LDES Server}
    member-type: {Defines the which syntax type is used to define the member id}
    shape: {URI to defined shape}
    timestamp-path: {SHACL property path to the timestamp when the version object entered the event stream.}
    version-of: {SHACL property path to the non-versioned identifier of the entity.}
  fragmentation:
    type: {timebased(default)/geospatial}
  ```
##### Example Mongo Configuration
  ```yaml
  spring.data.mongodb:
    uri: mongodb://{docker-hostname}:{port}
    database: {database name}
  ```

##### Example Timebased Fragmentation
  ```yaml
  timebased:
    memberLimit: {member limit}
  ```
##### Example Geospatial Fragmentation
  ```yaml
  geospatial:
    maxZoomLevel: {Required zoom level}
    bucketiserproperty: {Defines which property will be used for bucketizing}
    projection: {"lambert72" (current only this projection is supported)}
  ```
### Docker

#### Docker-compose

There are 2 files where you can configure the dockerized application:
- [The config files](#the-config-files)
- [The docker-compose file](#the-docker-compose-file)

#### The Config Files

Runtime settings can be defined in the configuration files. Use [config.env](docker-compose/config.env) for a public setup and be sure not to commit this file, as it contains secrets. For a local setup, use [config.local.env](docker-compose/config.local.env).

#### The docker-compose File

Change the `env_file` to `config.env` or `config.local.env` in [docker-compose.yml](docker-compose.yml) according to your needs.

#### Starting the Dockerized Application

Run the following commands to start the containers:

```bash
COMPOSE_DOCKER_CLI_BUILD=1 docker-compose build
docker-compose up
```

## Developer Information

### How To Build

For compilation of the source code, execute the following command

```
mvn clean compile
```

### How To Test

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
- ldes-fragmentisers-sequential-timebased
- ldes-fragmentisers-geospatial