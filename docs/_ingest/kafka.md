---
layout: default
title: Ingest Members With Kafka
nav_order: 0
---

# Ingest Members With Kafka

Ingesting members into an Event Stream without too much overhead? 
That's now possible via ingestion over Apache Kafka.

## Getting Started

To get started with ingesting members via Kafka, you need to have the following:
* Kafka consumer configuration (in the Application Properties)
* Event Stream configuration pointing to a Kafka topic (in the Admin API) 

### Application Properties

The Kafka consumer configuration can be set in the `application.properties` file.

The most basic properties that are needed are:
````yaml
spring.kafka.consumer.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=my-group
````

To guarantee that the Kafka consumer will always read from the beginning of the topic, you can add the following property:
````yaml
spring.kafka.consumer.auto-offset-reset=earliest
````

For more advanced options to configure advanced Kafka connections, please refer to the [Spring Kafka documentation](https://docs.spring.io/spring-boot/appendix/application-properties/index.html).

### Event Stream Configuration

To configure a new Event Stream that uses Kafka as the ingestion method, 
you need to create an Event Stream configuration that points to a Kafka topic. \
This can be done by adding a `https://w3id.org/ldes#KafkaEventStream` object to the Event Stream configuration.

This object should contain the following properties:
* `ldes:topic` - The Kafka topic to which the members should be ingested.
* `ldes:mimeType` - The mime type in which the data of your topic will be. This is used to parse your member to a model. \
    This can be `application/ld+json`, `application/json`, `text/turtle`, ... \
    All members in your topic need to therefor be in one mime type.

#### Example

Creating a generic Event Stream named "event-stream" that uses Kafka as the ingestion method.
    
````turtle
@prefix ldes:           <https://w3id.org/ldes#> .
@prefix dcterms:        <http://purl.org/dc/terms/> .
@prefix prov:           <http://www.w3.org/ns/prov#> .
@prefix tree:           <https://w3id.org/tree#>.
@prefix sh:             <http://www.w3.org/ns/shacl#> .
@prefix server:         <http://localhost:8080/> .
@prefix xsd:            <http://www.w3.org/2001/XMLSchema#> .
@prefix event-stream:   <http://localhost:8080/event-stream/> .

server:event-stream a ldes:EventStream ;
	ldes:timestampPath dcterms:created ;
	ldes:versionOfPath dcterms:isVersionOf ;
	tree:shape [ a sh:NodeShape ] ;
        ldes:kafkaSource [
		ldes:topic "testTopic" ;
		ldes:mimeType "application/n-quads" ;
	] .

````