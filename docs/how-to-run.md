---
title: How To Run
layout: home
nav_order: 1
---

# How to run 

We advise running the LDES Server as a Docker Image which we provide via [Docker Hub](https://hub.docker.com/r/ldes/ldes-server/):

* Latest Official version: [![Docker Image Version](https://img.shields.io/docker/v/ldes/ldes-server/latest)](https://hub.docker.com/r/ldes/ldes-server/tags)
* Latest Alpha version: [![Docker Image Version](https://img.shields.io/docker/v/ldes/ldes-server)](https://hub.docker.com/r/ldes/ldes-server/tags)

To decide which version to take, visit the [Release Management Advice](https://informatievlaanderen.github.io/VSDS-Tech-Docs/release/Release_Management#which-version-should-i-use)
 and visit the [LDES Server Github Release Page](https://github.com/Informatievlaanderen/VSDS-LDESServer4J/releases/) for an overview of all the releases.

## LDES Server Config

The LDES Server provides a variety of tweaking options to configure it to your ideal use case:

| Type           | Property                                | Description                                                                                                                                                                        |
|----------------|-----------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Swagger Config | springdoc.swagger-ui.path               | As the LDES Server provides an Swagger API to easily configure your Streams, a url needs to be configured that points to the swagger documentation.                                |
| Server URL     | ldes-server.host-name                   | This is the url that will be used throughout the fragment names. <br>This should therefor point to a publicly available url.                                                       |
| Mongo config   |                                         | As of this moment the LDES Server only supports a MongoDB implementation. <br> The following properties have to be set to provide connectivity between the server and the database |
|                | spring.data.mongodb.host                | URL that points to the MongoDB server                                                                                                                                              |
|                | spring.data.mongodb.port                | Port on which the MongoDB server runs                                                                                                                                              |
|                | spring.data.mongodb.database            | Name for the existing or to be created database on the MongoDB server                                                                                                              |
|                | spring.data.mongodb.auto-index-creation | Enablement of the automatic creation of indexes. <br> We highly advise you to keep this on for performance reasons                                                                 |


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