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
  data:
    mongodb:
      host: ldes-mongodb
      port: 27017
      database: ldes
      auto-index-creation: true
````

Here is an explanation provided for all the possibilities on how to tweak and configure your LDES Server:

<table>
<thead>
  <tr><th colspan="4">Feature</th></tr>
  <tr><th>Property</th><th>Description</th><th>Required</th><th>Default value</th></tr>
</thead>
<tbody>
  <tr><td colspan="4"><b>Open-API</b></td></tr>
  <tr>
    <td>springdoc.api-docs.enabled</td>
    <td>This boolean indicates whether Open API documentation is enabld. <a href="https://springdoc.org/#properties">More Open-API documentation</a></td>
    <td>No</td>
    <td>true</td>
  </tr>
  <tr>
    <td>springdoc.api-docs.path</td>
    <td>When enabled, an url* needs to be configured that points to the Open API documentation. <a href="https://springdoc.org/#properties">More Open-API documentation</a></td>
    <td>No</td>
  </tr>
  <tr><td colspan="4"><b>Swagger UI</b></td></tr>
  <tr>
    <td>springdoc.swagger-ui.enabled</td>
    <td>This boolean indicates whether Swagger API is enabled. This can be used to easily configure your Streams. <a href="https://springdoc.org/#swagger-ui-properties">More Swagger UI documentation</a></td>
    <td>No</td>
    <td></td>
  </tr>
  <tr>
    <td>springdoc.swagger-ui.path</td>
    <td>When enabled, an url* needs to be configured that points to the Swagger documentation. <a href="https://springdoc.org/#swagger-ui-properties">More Swagger UI documentation</a></td>
    <td>No</td>
    <td>true</td>
  </tr>
  <tr><td colspan="4"><b>URL Configuration</b></td></tr>
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
  <tr><td colspan="4"><b>MongoDB Storage</b>**</td></tr>
  <tr>
    <td>spring.data.mongodb.host</td>
    <td>URL that points to the MongoDB server</td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>spring.data.mongodb.port</td>
    <td>Port on which the MongoDB server runs</td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>spring.data.mongodb.database</td>
    <td>Name for the existing or to be created database on the MongoDB server</td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>spring.data.mongodb.uri</td>
    <td>
      Alternative to the previous 3 properties, allows passing the mongodb connection string. Note that when a MongoDB link needs to be configured with authentication, it is typically done with an uri, e.g. <code>mongodb://myDatabaseUser:D1fficultP%40ssw0rd@mongodb0.example.com:27017/?authSource=admin</code>
    </td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>spring.data.mongodb.auto-index-creation</td>
    <td>Enables the server to automatically create indices in mongodb. If this property is not enabled, you have to manage the indices manually. This can have a significant impact on performance. <br> We highly advise you to keep this on for performance reasons</td>
    <td></td>
    <td></td>
  </tr>
  <tr><td colspan="4"><b><a href="./features/compaction">Fragment Compaction</a></b></td></tr>
  <tr>
    <td>ldes-server.compaction-cron</td>
    <td>Defines how often the Compaction Service will check the fragments ***</td>
    <td>No</td>
    <td>0 0 0 * * *</td>
  </tr>
  <tr>
    <td>ldes-server.compaction-duration</td>
    <td>Defines how long the redundant compacted fragments will remain on the server</td>
    <td>No</td>
    <td>PD7</td>
  </tr>
  <tr>
    <td>ldes-server.deletion-cron</td>
    <td>Defines how often the redundant compacted fragments will be checked for deletion ***</td>
    <td>No</td>
    <td>0 0 0 * * *</td>
  </tr>
  <tr><td colspan="4"><b>Retention (<a href="./configuration/retention-policies">Retention Policies</a>)</b></td></tr>
  <tr>
    <td>ldes-server.retention-cron</td>
    <td>Defines how often the Retention Service will check the members ***</td>
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
    <td>Defines on which port the admin endpoints are available ****</td>
    <td>No</td>
    <td>8080</td>
  </tr>
</tbody>
</table>

> **Note** *: The specified url will be prefixed by an optional `server.servlet.context-path`

> **Note** **: As of this moment the LDES Server only supports a MongoDB implementation. The following properties have
> to be set to provide connectivity between the server and the database


> **Note** ***: Unix usually supports a cron expression of 5 parameters, which excludes seconds. However, the spring
> annotation `@Scheduled` adds a 6th parameter to support seconds.
>
> More information about this can be found in
>
the [spring documentation](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html).

> **Note** ****: When using the swagger API with separate port bindings, the swagger API will always be available under the admin port.

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
