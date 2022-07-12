# LDES Server

The Linked Data Event Stream (LDES) Server is a configurable component that can be used to ingest, store, transform and (re-)publish an [LDES](https://semiceu.github.io/LinkedDataEventStreams/).
The LDES server was built in the context of the [VSDS project](https://vlaamseoverheid.atlassian.net/wiki/spaces/VSDSSTART/overview) in order to easily exchange open data.  

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Informatievlaanderen_VSDS-LDESServer4J&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Informatievlaanderen_VSDS-LDESServer4J)

## Table Of Contents

- [LDES Server](#ldes-server)
  * [Table Of Contents](#table-of-contents)
  * [Set-up of the LDES Server](#set-up-of-the-ldes-server)
  * [How to run](#how-to-run)
    + [Locally](#locally)
      - [Maven](#maven)
      - [Profiles](#profiles)
    + [Docker](#docker)
      - [Docker-compose](#docker-compose)
      - [The config files](#the-config-files)
      - [The docker-compose file](#the-docker-compose-file)
      - [Starting the dockerized application](#starting-the-dockerized-application)
  * [Developer Information](#developer-information)
    + [How to build](#how-to-build)
    + [How to test](#how-to-test)
      - [Unit and integration tests](#unit-and-integration-tests)
      - [Only unit tests](#only-unit-tests)
      - [Only integration tests](#only-integration-tests)

## Set-up of the LDES Server

The current implementation consists of 5 modules:
- ldes-server-application which starts the spring boot application
- ldes-server-domain which contains the domain logic of the ldes server
- ldes-server-infra-mongo which allows to store ldes members and fragments in a mongoDB
- ldes-server-port-ingestion-rest which allows to ingest ldes members via http
- ldes-server-port-publication-rest which allows to retrieve fragments via http

The modules ldes-server-infra-mongo, ldes-server-port-ingestion-rest and ldes-server-port-publication-rest are built so that they can be replaced by other implementations without the need for code changes in ldes-server-domain.

## How to run
We'll show you how to set up your own LDES Server both locally and via Docker using a mongo db for storage and a rest endpoint for ingestion and publication.
Afterwards, you can change storage, ingestion and publication options by plugging in other components.

### Locally

#### Maven

To locally run the LDES Server in maven, the following Maven command can be executed:

```mvn
mvn spring-boot:run
```

This will run a clean slate LDES Server depending on your needs, one or of the following maven profiles can be activated:

#### Profiles

- **Http Endpoints (Publication/Ingestion)**
    > _application config_:
    > ```yaml
    > server.port: {http-port}
    > ldes:
    >   collection-name: {short name of the collection}
    >   host-name: {endpoint of LDES Server}
    > view:
    >   shape: {URI to defined shape}
    >   member-limit: {limit how many fragment can exist inside fragment}
    >   timestamp-path: { LDES timestampPath }
    >   version-of-path: { LDES versionOfPath }
    > ```
    > see [LDES timestamp](https://w3id.org/ldes#timestampPath) & [LDES versionOfPath](https://w3id.org/ldes#versionOfPath) for more info
  - http-ingest: Enables a http endpoint for to insert LDES members.
    > Endpoint:
    > - URL: /ldes-member
    > - Request type: POST
    > - Accept: "application/n-quads"
  - http-publish: Enables a http endpoint to retrieve LDES fragments
    > Endpoint:
    > - URL: /{collection-name}?generatedAtTime={fragmentGenerationTimestamp}
    > - Request type: GET
    > - Accept: "application/n-quads", "application/json-ld"
- **Storage**
  - storage-mongo: Allows the LDES Server to read and write from a mongo database.
    > _application config_:
    > ```yaml
    > spring.data.mongodb:
    >   uri: mongodb://{docker-hostname}:{port}
    >   database: {database name} 
    > ```

### Docker

#### Docker-compose

There are 2 files where you can configure the dockerized application:
- [The config files](#the-config-files)
- [The docker-compose file](#the-docker-compose-file)

#### The config files

Runtime settings can be defined in the configuration files. Use [config.env](docker-compose/config.env) for a public setup and be sure not to commit this file, as it contains secrets. For a local setup, use [config.local.env](docker-compose/config.local.env).

#### The docker-compose file

Change the `env_file` to `config.env` or `config.local.env` in [docker-compose.yml](docker-compose.yml) according to your needs.

#### Starting the dockerized application

Run the following commands to start the containers:

```bash
COMPOSE_DOCKER_CLI_BUILD=1 docker-compose build
docker-compose up
```

## Developer Information

### How to build

For compilation of the source code, execute the following command

```
mvn clean compile
```

### How to test

#### Unit and integration tests

For running all the tests of the project, execute the following command

```
mvn clean verify
```

#### Only unit tests

For running the unit tests of the project, execute the following command

```
mvn clean verify -Dintegrationtestskip=true
```

#### Only integration tests

For running the integration tests of the project, execute the following command

```
mvn clean verify -Dunittestskip=true
```