---
layout: default
title: Reference Fragmentation
nav_order: 1
parent: Fragmentations
---

# Reference fragmentation

Reference fragmentation will create fragments based on a provided property path defined as `tree:fragmentationPath`.
When no fragmentationPath is defined, "rdf:type" is used by default.
This allows you to fragment the data on references.

## Properties

  ```ttl
  @prefix tree: <https://w3id.org/tree#> .
  
  tree:fragmentationStrategy [
        a tree:ReferenceFragmentation ;
        tree:fragmentationPath { Optional: defines which property will be used for bucketizing } ;
    ] .
  ```

## Algorithm

1. The fragmentationObjects of the member are determined
   - We filter the RDF statements where the property path matches the `fragmentationPath`
   - We select all the object that pass the above filters.
2. A bucket of references is created using the object value(s)
3. The buckets are iterated. The member is added to every bucket. Taking into account:
   - A new fragment is created if no fragment exists for the given reference.
   - The member is added to every related fragment

![](../../../ldes-fragmentisers/ldes-fragmentisers-reference/content/reference_algorithm.svg)

## Example

Example properties:

  ```ttl
  @prefix tree: <https://w3id.org/tree#> .
  
  tree:fragmentationStrategy [
        a tree:ReferenceFragmentation ;
        tree:fragmentationPath "<http://purl.org/dc/terms/isVersionOf>" ;
    ] .
  ```

With following example input:

```ttl
@prefix dc: <http://purl.org/dc/terms/> .
@prefix ns0: <http://semweb.mmlab.be/ns/linkedconnections#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix ns1: <http://vocab.gtfs.org/terms#> .
@prefix prov: <http://www.w3.org/ns/prov#> .
@prefix ns2: <http://www.opengis.net/ont/geosparql#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> .

<http://njh.me/original-id/123#2022-09-28T17:11:28.520Z>
  dc:isVersionOf <http://njh.me/original-id/123> ;
  ns0:arrivalStop <http://example.org/stops/402161> ;
  ns0:arrivalTime "2022-09-28T07:14:00.000Z"^^xsd:dateTime ;
  ns0:departureStop <http://example.org/stops/402303> ;
  ns0:departureTime "2022-09-28T07:09:00.000Z"^^xsd:dateTime ;
  ns1:dropOffType ns1:Regular ;
  ns1:pickupType ns1:Regular ;
  ns1:route <http://example.org/routes/Hasselt_-_Genk> ;
  ns1:trip <http://example.org/trips/Hasselt_-_Genk/Genk_-_Hasselt/20220928T0909> ;
  a ns0:Connection ;
  prov:generatedAtTime "2022-09-28T17:11:28.520Z"^^xsd:dateTime .
```

The selected object would be

`<http://njh.me/original-id/123>`

After ingestion the member will be part of the following fragment
- http://localhost:8080/addresses/by-version?reference=<http://njh.me/original-id/123>
