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
      ingest state objects.
      The default value of this object is `false` and the property can be omitted.

    * For more info, visit the Swagger API endpoint configured in [the run guide.](../how-to-run)

### Create version objects vs ingest version objects

Until version `2.11.0`, only version objects could be ingested. But from version `2.12.0`, either version objects or
state objects could be ingested in an event stream. This results in a slightly other meaning for both the `ldes:timestampPath`
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
    ldes:createVersions true
    tree:shape genericES:shape .

genericES:shape a sh:NodeShape .
````

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

Defining a shape can be done through the `/admin/api/eventstreams/{collectionName}/shape` endpoint.

For more info, visit the Swagger API endpoint configured in [the run guide.](../how-to-run)

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
