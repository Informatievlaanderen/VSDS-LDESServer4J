# LDES Server

## About

The LDES Server is a configurable component that can be used to ingest, store, transform and (re-)publish an LDES.

## How to use the LDES Server
We'll show you how to set up your own LDES Server both locally and via Docker using a mongo db for storage and a rest endpoint for ingestion and publication.
Afterwards, you can change storage, ingestion and publication options by plugging in other components.

## Locally

### Docker-compose

There are 3 files where you can configure the dockerized application:
- [The config files](#docker-compose-config-config)
- [The docker-compose file](#docker-compose-config-yml)

#### <a name="docker-compose-config-config"></a>The config files: [config.env](docker-compose/config.env) or [config.local.env](docker-compose/config.local.env)

Runtime settings can be defined in the configuration files. Use [config.env](docker-compose/config.env) for a public setup and be sure not to commit this file, as it contains secrets. For a local setup, use [config.local.env](docker-compose/config.local.env). It's safe to commit this file with secrets, since they'll only be relevant to your local environment (assuming you don't reuse personal passwords for public services for your local services).

#### <a name="docker-compose-config-yml"></a> The docker-compose file: [docker-compose.yml](docker-compose.yml)

Change the `env_file` to `config.env` or `config.local.env` according to your needs.

#### Starting the dockerized application

Run the following commands to start the containers:

```bash
COMPOSE_DOCKER_CLI_BUILD=1 docker-compose build
docker-compose up
```

### Maven

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