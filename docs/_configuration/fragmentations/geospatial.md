---
layout: default
title: Geospatial Fragmentation
nav_order: 1
parent: Fragmentations
---

# Geospatial fragmentation

Geospatial fragmentation will create fragments based on geospatial tiles selected of the `fragmentationPath`.
This allows you to fragment the data on geolocations.

## Properties

  ```turtle
  @prefix tree: <https://w3id.org/tree#> .
  
  tree:fragmentationStrategy [
        a tree:GeospatialFragmentation ;
        tree:maxZoom { Mandatory: Required zoom level } ;
        tree:fragmentationPath { Mandatory: defines which property will be used for bucketizing } ;
        tree:fragmenterSubjectFilter { Optional: regex to filter the subjects matching the fragmentationPath } ;
    ] .
  ```

## Algorithm

1. The fragmentationObjects of the member are determined
    - We filter the RDF statements where the predicate matches the `fragmentationPath`
    - If an optional regex is provided through the `fragmenterSubjectFilter` property, we filter on subjects that match this regex.
    - We select all the object that pass the above filters.
2. A bucket of tiles is created using the coordinates and provided zoomLevel. [This is done using the Slippy Map algorithm.](https://wiki.openstreetmap.org/wiki/Slippy_map)
3. The tiles are iterated. The member is added to every tile, or sub-fragmentations of these tiles. Taking into account:
    - A new fragment is created if no fragment exists for the given tile.
    - There is no `memberLimit` or max size for a fragment. They do not become immutable.
    - The member is added to every related fragment

````mermaid
flowchart TD
    A[First statement is selected where the 
    predicate matches fragmenterProperty 
    AND subject matches fragmenterSubjectFilter] --> B
    B[Coordinates of this statement are selected] --> C
    C[Bucker of tiles are
    created using the coordinates 
    and zoomLevel] --> D{Next tile?}
    
    D --> |true| E{Fragment for 
    tile exists?}
    E --->|false| F[Create Fragment]
    E -->|true| G[Add member to fragment]
    F ----> G
    D -------> |false| END(END)
    G --> D
  
````
## Example

Example properties:

  ```turtle
  @prefix tree: <https://w3id.org/tree#> .
  
  tree:fragmentationStrategy [
        a tree:GeospatialFragmentation ;
        tree:maxZoom 15 ;
        tree:fragmentationPath <http://www.opengis.net/ont/geosparql#asWKT> ;
    ] .
  ```

With following example input:

```turtle
@prefix dc: <http://purl.org/dc/terms/> .
@prefix ns0: <http://semweb.mmlab.be/ns/linkedconnections#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix ns1: <http://vocab.gtfs.org/terms#> .
@prefix prov: <http://www.w3.org/ns/prov#> .
@prefix ns2: <http://www.opengis.net/ont/geosparql#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> .

<http://njh.me/original-id#2022-09-28T17:11:28.520Z>
  dc:isVersionOf <http://njh.me/original-id> ;
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

<http://example.org/stops/402161>
  ns2:asWKT "POINT (5.47236 50.9642)"^^ns2:wktLiteral ;
  a ns1:Stop ;
  rdfs:label "Genk Brug" ;
  geo:lat 5.096420e+1 ;
  geo:long 5.472360e+0 .

<http://example.org/stops/402303>
  ns2:asWKT "POINT (5.49661 50.9667)"^^ns2:wktLiteral ;
  a ns1:Stop ;
  rdfs:label "Genk Station perron 11" ;
  geo:lat 5.096670e+1 ;
  geo:long 5.496610e+0 .
```

The selected objects would be

`"POINT (5.47236 50.9642)"^^ns2:wktLiteral` and `"POINT (5.49661 50.9667)"^^ns2:wktLiteral`

When we convert these [coordinates to tiles](https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Lon..2Flat._to_tile_numbers_2), the bucket of tiles would be:
- "15/16884/10974"
- "15/16882/10975"

### When geospatial fragmentation is the lowest level

After ingestion the member will be part of the following two fragments
- http://localhost:8080/addresses/by-zone?tile=15/16884/10974&pageNumber=1
- http://localhost:8080/addresses/by-zone?tile=15/16882/10975&pageNumber=1