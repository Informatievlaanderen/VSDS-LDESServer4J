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

The LDES Server provides a variety of tweaking options to configure it to your ideal use case.

An example basic config can be found here:

***ldes-server.yml***:

````yaml
springdoc:
  swagger-ui:
    path: /v1/swagger
ldes-server:
  host-name: "http://localhost:8080"
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/test
    username: admin
    password: admin
  batch:
    jdbc:
      initialize-schema: always
      
rest:
  max-age: 120
  max-age-immutable: 604800
````

Here is an explanation provided for all the possibilities on how to tweak and configure your LDES Server:

<table>
<thead>
  <tr><th colspan="4">Feature</th></tr>
  <tr><th>Property</th><th>Description</th><th>Required</th><th>Default value</th></tr>
</thead>
<tbody>
  <tr><td colspan="4"><b>API endpoints documentation</b></td></tr>
  <tr>
    <td>springdoc.api-docs.path</td>
    <td>The url<sup>1</sup> that will point to the Open API documentation. <a href="https://springdoc.org/#properties">More Open-API documentation</a></td>
    <td>No</td>
  </tr>
  <tr>
    <td>springdoc.swagger-ui.path</td>
    <td>The url<sup>1</sup> that will point to the Swagger documentation. <a href="https://springdoc.org/#swagger-ui-properties">More Swagger UI documentation</a></td>
    <td>No</td>
    <td>true</td>
  </tr>
  <tr><td colspan="4"><b>URL Configuration</b></td></tr>
  <tr>
    <td>server.address</td>
    <td>This is the url that will be used by the server application to bind to. This is especially useful when exposing actuator endpoints publicly. However, it must be known if the address cannot be found or is unavailable, the application will be unable to start.</td>
    <td>No</td>
    <td></td>
  </tr>
  <tr>
    <td>ldes-server.host-name</td>
    <td>This is the url that will be used throughout the fragment names. This should therefor point to a resolvable url.</td>
    <td>Yes</td>
    <td></td>
  </tr>
  <tr>
    <td>ldes-server.use-relative-url</td>
    <td>Determines if the resources hosted on the server are constructed with a relative URI. For <a href="./features/relative-urls">more</a></td>
    <td>No</td>
    <td>false</td>
  </tr>
  <tr><td colspan="4"><b>Ingest/Fetch</b></td></tr>
  <tr>
    <td>rest.max-age</td>
    <td>Time in seconds that a mutable fragment can be considered up-to-date</td>
    <td>No</td>
    <td>60</td>
  </tr>
  <tr>
    <td>rest.max-age-immutable</td>
    <td>Time in seconds that an immutable fragment should not be refreshed</td>
    <td>No</td>
    <td>604800</td>
  </tr>
  <tr>
    <td>ldes-server.formatting.prefixes</td>
    <td><a href="./features/rdf-prefixes">Prefixes</a> that will be used in the LDES Server when fetching data from the server in text/turtle format</td>
    <td>No</td>
    <td></td>
  </tr>
  <tr><td colspan="4"><b>PostgreSQL Storage</b><sup>2</sup></td></tr>
  <tr>
    <td>spring.datasource.url</td>
    <td>URL that points to the PostgreSQL server</td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>spring.datasource.username</td>
    <td>Username to log into provided PostgreSQL instance</td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>spring.datasource.password</td>
    <td>Password to log into provided PostgreSQL instance</td>
    <td></td>
    <td></td>
  </tr>
<tr><td colspan="4"><b>Bucketisation & pagination batching</b></td></tr>
  <tr>
    <td>ldes-server.fragmentation-cron</td>
    <td>Defines how often Fragmentation Service will check for unprocessed members (when present, trigger fragmentation job).</td>
    <td>No</td>
    <td>*/30 * * * * *</td>
  </tr>
  <tr><td colspan="4"><b><a href="./features/compaction">Fragment Compaction</a></b></td></tr>
  <tr>
    <td>ldes-server.compaction-duration</td>
    <td>Defines how long the redundant compacted fragments will remain on the server</td>
    <td>No</td>
    <td>PD7</td>
  </tr>
  <tr><td colspan="4"><b>Maintenance</b></td></tr>
  <tr>
    <td>ldes-server.maintenance-cron</td>
    <td>Defines how often the maintenance job will run, which includes <a href="./features/retention-policies">retention</a>, <a href="./features/compaction">compaction and deletion</a><sup>3</sup></td>
    <td>No</td>
    <td>0 0 0 * * *</td>
  </tr>
  <tr><td colspan="4"><b>Ports</b></td></tr>
  <tr>
    <td>ldes-server.ingest.port</td>
    <td>Defines on which port the ingest endpoint is available</td>
    <td>No</td>
    <td>8080</td>
  </tr>
  <tr>
    <td>ldes-server.fetch.port</td>
    <td>Defines on which port the fetch endpoints are available</td>
    <td>No</td>
    <td>8080</td>
  </tr>
  <tr>
    <td>ldes-server.admin.port</td>
    <td>Defines on which port the admin endpoints are available<sup>4</sup></td>
    <td>No</td>
    <td>8080</td>
  </tr>
</tbody>
</table>

> **Note** 1: The specified url will be prefixed by an optional `server.servlet.context-path`

> **Note** 2: Since the 3.0 release, the MongoDB got replaced with a PostgreSQL implementation.
> For Migration instructions, please check the below migration paragraph.


> **Note** 3: Unix usually supports a cron expression of 5 parameters, which excludes seconds. However, the spring
> annotation `@Scheduled` adds a 6th parameter to support seconds. The cron schedules are in timezone 'UTC'.
>
> More information about this can be found in the [spring documentation]

> **Note** 4: When using the swagger API with separate port bindings, the swagger API will always be available under
> the admin port.

## Docker Compose

````yaml
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
      - ldes-postgres
  ldes-postgres:
    container_name: ldes-postgres
    image: postgres:14-alpine
    ports:
      - 5432:5432
    environment:
      - POSTGRES_PASSWORD=admin
      - POSTGRES_USER=admin
      - POSTGRES_DB=test
    networks:
      - ldes
networks:
  ldes:
    name: quick_start_network
````

## Migration to 3.0

Since the Mongodb implementation got replaced with a PostgreSQL one, a migration path has been provided.
This migration path is only needed for those who wish to move their 2.x LDES servers to the latest version.

To enable this, please first take in the 3.0.0 release and add the following properties to your config:

````yaml
ldes-server:
  migrate-mongo: true
spring:
  data:
    mongodb:
      uri: # connection string pointing to mongodb instance
  batch:
    jdbc:
      initialize-schema: always
````

> **_ALTERNATIVE MIGRATION:_**  Besides the provided migration solution, It is also possible to set up your new 3.x Server 
> next to your 2.x Server and then use and LDES workbench to send over the data from the 2.x Server to the 3.x Server.
> This pipeline in [LDIO] should contain an [LDES Client] to read data from the 2.x Server and an [LDIO Http Out] to send data towards the 3.x Server.


[LDIO]: https://informatievlaanderen.github.io/VSDS-Linked-Data-Interactions/ldio
[LDES Client]: https://informatievlaanderen.github.io/VSDS-Linked-Data-Interactions/ldio/ldio-inputs/ldio-ldes-client
[LDIO Http Out]: https://informatievlaanderen.github.io/VSDS-Linked-Data-Interactions/ldio/ldio-outputs/ldio-http-out
[spring documentation]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html