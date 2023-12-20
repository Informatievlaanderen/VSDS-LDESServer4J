---
title: How To Run
layout: home
nav_order: 1
---

# How to run

We advise running the LDES Server as a Docker Image which we provide
via [Docker Hub](https://hub.docker.com/r/ldes/ldes-server/):

* Latest Official
  version: [![Docker Image Version](https://img.shields.io/docker/v/ldes/ldes-server/latest)](https://hub.docker.com/r/ldes/ldes-server/tags)
* Latest Alpha
  version: [![Docker Image Version](https://img.shields.io/docker/v/ldes/ldes-server)](https://hub.docker.com/r/ldes/ldes-server/tags)

To decide which version to take, visit
the [Release Management Advice](https://informatievlaanderen.github.io/VSDS-Tech-Docs/release/Release_Management#which-version-should-i-use)
and visit the [LDES Server Github Release Page](https://github.com/Informatievlaanderen/VSDS-LDESServer4J/releases/) for
an overview of all the releases.

## LDES Server Config

The LDES Server provides a variety of tweaking options to configure it to your ideal use case:

| Feature             | Property                                | Description                                                                                                                                                                                                                                                                                                      | Default Value |
|---------------------|-----------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| Open-API            | springdoc.api-docs.enabled              | Set `springdoc.api-docs.enabled: true` to enable Open API documentation. [More Open-API documentation](https://springdoc.org/#properties) |               |
| Open-API            | springdoc.api-docs.path                 | When enabled, a url needs to be configured that points to the Open API documentation, i.e. `springdoc.api-docs.path: /v1/api-docs`. Note that this will be prefixed by an optional `server.servlet.context-path`. [More Open-API documentation](https://springdoc.org/#properties)  |               |
| Swagger Config      | springdoc.swagger-ui.enabled            | Set `springdoc.swagger-ui.enabled: true` to enable a Swagger API to easily configure your Streams. [More Swagger UI documentation](https://springdoc.org/#swagger-ui-properties) |               |
| Swagger Config       | springdoc.swagger-ui.path               | When enabled, a url needs to be configured that points to the Swagger documentation, i.e. `springdoc.swagger-ui.path: /v1/api-docs`. Note that this will be prefixed by an optional `server.servlet.context-path`. [More Swagger UI documentation](https://springdoc.org/#swagger-ui-properties)  |               |
|                     |                                         |                                                                                                                                                                                                                                                                                                                  |               |
| URL Configuration   | ldes-server.host-name                   | This is the url that will be used throughout the fragment names. <br>This should therefor point to a publicly available url.                                                                                                                                                                                     |               |
|                     | 
| Ingest/Fetch        | 
|                     | rest.max-age                            | Time in seconds that a mutable fragment can be considered up-to-date                                                                                                                                                                                                                                             | 60            |
|                     | rest.max-age-immutable                  | Time in seconds that an immutable fragment should not be refreshed                                                                                                                                                                                                                                               | 604800        |
|                     |                                         |                                                                                                                                                                                                                                                                                                                  |               |
| MongoDB Storage     |                                         | As of this moment the LDES Server only supports a MongoDB implementation. <br> The following properties have to be set to provide connectivity between the server and the database                                                                                                                               |               |
|                     | spring.data.mongodb.host                | URL that points to the MongoDB server                                                                                                                                                                                                                                                                            |               |
|                     | spring.data.mongodb.port                | Port on which the MongoDB server runs                                                                                                                                                                                                                                                                            |               |
|                     | spring.data.mongodb.database            | Name for the existing or to be created database on the MongoDB server                                                                                                                                                                                                                                            |               |
|                     | spring.data.mongodb.uri                 | Alternative to the previous 3 properties, allows passing the mongodb connection string. Note that when a MongoDB link needs to be configured with authentication, it is typically done with a uri, e.g. `mongodb://myDatabaseUser:D1fficultP%40ssw0rd@mongodb0.example.com:27017/?authSource=admin`                                                                                                                                                                                                                         |               |
|                     | spring.data.mongodb.auto-index-creation | Enables the server to automatically create indices in mongodb. If this property is not enabled, you have to manage the indices manually. This can have a significant impact on performance. <br> We highly advise you to keep this on for performance reasons                                                    |               |
|                     |                                         |                                                                                                                                                                                                                                                                                                                  |               |
| Fragment Compaction |                                         | [Fragment Compaction](./features/compaction)                                                                                                                                                                                                                                                                     |               |                                                                                                                                 |
|                     | ldes-server.compaction-cron             | Defines how often the Compaction Service will check the fragments                                                                                                                                                                                                                                                | 0 0 0 * * *   |
|                     | ldes-server.compaction-duration         | Defines how long long the redundant compacted fragments will remain on the server                                                                                                                                                                                                                                | PD7           |
|                     | ldes-server.deletion-cron               | Defines how often the redundant compacted fragments will be checked for deletion                                                                                                                                                                                                                                 | 0 0 0 * * *   |
|                     |                                         |                                                                                                                                                                                                                                                                                                                  |               |
| Retention           |                                         | [Retention Policies](./configuration/retention-policies)                                                                                                                                                                                                                                                         |               |
|                     | ldes-server.retention-cron              | Defines how often the Retention Service will check the members                                                                                                                                                                                                                                                   | 0 0 0 * * *   |

> **Note**: Unix usually supports a cron expression of 5 parameters, which excludes seconds. However, the spring
> annotation `@Scheduled` adds a 6th parameter to support seconds.
>
> More information about this can be found in
> the [spring documentation](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html).

Based on the previous config options, we provide a basic config to use.

***ldes-server.yml***:

````yaml
springdoc:
  swagger-ui:
    path: /v1/swagger
ldes-server:
  host-name: "http://localhost:8080"
spring:
  data:
    mongodb:
      host: ldes-mongodb
      port: 27017
      database: ldes
      auto-index-creation: true
````

## Docker Compose

````yaml
version: "3.3"
services:
  ldes-server:
    container_name: basic_ldes-server
    image: ldes/ldes-server
    environment:
      - SPRING_CONFIG_LOCATION=/config/
    volumes:
      - ./ldes-server.yml:/config/application.yml:ro
    ports:
      - 8080:8080
    networks:
      - ldes
    depends_on:
      - ldes-mongodb
  ldes-mongodb:
    container_name: ldes-mongodb
    image: mongo
    ports:
      - 27017:27017
    networks:
      - ldes
networks:
  ldes:
    name: quick_start_network
````
