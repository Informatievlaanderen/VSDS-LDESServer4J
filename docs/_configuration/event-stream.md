---
layout: default
title: Configuring a new Event Stream
nav_order: 1
---

# Configuring a new Event Stream

To host a new Event Stream on your server, it first needs to be configured.
This can be done through the Admin API at the `/admin/api/v1/eventstreams` endpoint.

An Event Stream config needs to contain a couple of items:

* a `ldes:EventStream` object containing:
    * `ldes:timestampPath` object that defines which object property it should parse to handle timebased fragmentations,
      retention policies, ...
    * `ldes:versionOfPath` object that defines which object property it should parse to handle version based retention
      policies.
      This property also indicates which state object your version object is a snapshot of.
    * `ldes:createVersions` object that defines whether the LDES should create version objects, indicating the LDES can
      ingest state objects. \
      The default value of this object is `false` and the property can be omitted.
    * `ldes:versionDelimiter` object that defines how the version object id will be constructed. (versionOf +
      delimiter + dateObserved) \
      The default value of this object is `/` and the property can be omitted.
    * `ldes:eventSource` object that defines which members are to be retained in the event stream.
      When omitted, all members are retained. More info on this can be
      found [here](./event-stream#configuring-member-deletion-on-an-event-stream)
    * `ldes:skolemizationDomain` object that defines the [skolemization](../features/skolemization) domain.
      Using `"http://example.com"` as domain will result in all blank nodes being transformed to
      `http://example.org/.well-known/genid/{unique_id}`.

    * For more info, visit the [Swagger API documentation.](./admin-api)

### Create version objects vs ingest version objects

Until version `2.11.0`, only version objects could be ingested. But from version `2.12.0`, either version objects or
state objects could be ingested in an event stream. This results in a slightly other meaning for both the
`ldes:timestampPath`
and `ldes:versionOfPath` properties.

In case of **version ingestion**, those properties are used to extract the information of the member. \
In case of **version creation**, those properties are used to append information to the member on fetching.

### Example

Creating a generic Event Stream named "generic-eventstream"

````turtle
@prefix ldes: <https://w3id.org/ldes#> .
@prefix dcterms: <http://purl.org/dc/terms/> .
@prefix tree: <https://w3id.org/tree#>.
@prefix sh:   <http://www.w3.org/ns/shacl#> .
@prefix server: <http://localhost:8080/> .
@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
@prefix genericES: <http://localhost:8080/generic-eventstream/> .

server:generic-eventstream a ldes:EventStream ;
    ldes:timestampPath dcterms:created ;
    ldes:versionOfPath dcterms:isVersionOf ;
    ldes:createVersions true ;
    tree:shape genericES:shape .

genericES:shape a sh:NodeShape .
````

## Configuring an Event Stream with a Kafka source

To configure an Event Stream that ingests members from a Kafka topic, please visit the [Kafka Ingest documentation](../ingest/kafka).

## Configuring a SHACL Shape

[SHACL (Shapes Constraint Language)](https://www.w3.org./TR/shacl/) is a standard for validating RDF data and ensuring
that it conforms to a particular structure or shape.
In the context of the Linked Data Event Stream (LDES), SHACL shapes are used to provide
a machine-readable description of the expected structure of members in the stream.

By incorporating SHACL shapes, LDES provides a powerful tool for ensuring data quality
and consistency, making it a reliable and trustworthy source of data for various
applications.
By defining a SHACL shape for the LDES, data producers can ensure that the members
they add to the LDES adhere to the required structure, while data consumers can use
the shape to validate and reason about the data they receive.

Defining a shape can be done through the `/admin/api/v1/eventstreams/{collectionName}/shape` endpoint.

For more info, visit the [Swagger API documentation.](./admin-api)

### Example

````turtle
@prefix sh:   <http://www.w3.org/ns/shacl#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .

[] a sh:NodeShape;
  sh:targetClass <https://w3id.org/ldes#EventStream> ;
  sh:closed true;
  sh:ignoredProperties (rdf:type) ;
  sh:property [
    sh:class sh:NodeShape;
    sh:description "The schema all elements of the eventstream must conform to."@en;
    sh:maxCount 1;
    sh:minCount 1;
    sh:name "shape"@en;
    sh:path <https://w3id.org/tree#shape>
  ], [
    sh:nodeKind sh:IRI ;
    sh:description "The object property of the members that idicates how members relate to each other from the time perspective."@en;
    sh:maxCount 1;
    sh:name "timestampPath"@en;
    sh:path <https://w3id.org/ldes#timestampPath>
  ], [
    sh:nodeKind sh:IRI ;
    sh:description "The object property that indicates the object identifier in a version object."@en;
    sh:maxCount 1;
    sh:name "versionOfPath"@en;
    sh:path <https://w3id.org/ldes#versionOfPath>
  ] .
````

## Configuring member deletion on an Event Stream

To determine which members should be permanently deleted from the Event Stream, it is necessary to set one or more
retention policies on the event source of the Event Stream.
Definition of event source:

> In Linked Data Event Streams, the ldes:EventSource class is designed to be the source for all derived views. The
> Linked Data Event Streams specification can also further elaborate on the ViewDescription by for example describing a
> retention policy on top of it.

By default, no retention policy is set on the event source meaning that no data is removed from the Event Stream. Even
when all views are deleted, the members will not be deleted from the Event Stream.

When a retention policy is set on the event source of an Event Stream, every member that is not part of any view and
which falls outside of the retention policy will be removed from the Event Stream.
More information on which retention policies can be used can be found [here](./retention-policies/index).

The event source is automatically created when creating an Event Stream but does not contain a retention policy by
default.
To add retention policies to the event source, the admin API can be used. More info on this can be
found [here](./admin-api).
It is only possible to add or edit retention policies of an event source, other properties cannot be changed.

Before introduction of the event source in the LDES Server, members were directly deleted when they weren't part of any
view anymore.
With the event source, it is possible to delete all views of an Event Stream without losing members so that you can
create new views without having to ingest data again.
If members need to be deleted directly from the Event Stream when they aren't part of any view, a timebased retention
policy of a few seconds can be set on the event source of the Event Stream.

### Example:

````turtle
@prefix ldes: <https://w3id.org/ldes#> .

ldes:retentionPolicy [
        a ldes:LatestVersionSubset ;
        ldes:amount 0 ;
      ] ;
````