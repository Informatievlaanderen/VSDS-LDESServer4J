---
layout: default
title: Timebased Fragmentation
nav_order: 1
parent: Fragmentations
---

# Timebased fragmentation

Timebased fragmentation will create fragments based on a time selected from the fragmentationPath and a given granularity.

## Properties

  ```turtle
  @prefix tree: <https://w3id.org/tree#> .
  
  tree:fragmentationStrategy [
        a tree:HierarchicalTimeBasedFragmentation ;
        tree:maxGranularity { Mandatory: defines the depth level of the fragments } ;
        tree:fragmentationPath { Mandatory: defines which property will be used for bucketizing } ;
        tree:fragmenterSubjectFilter { Optional: regex to filter the subjects matching the fragmentationPath } ;
    ] .
  ```
For maxGranularity the following values are allowed: 
* year,
* month,
* day,
* hour,
* minute,
* second.

## Algorithm

1. The fragmentationObjects of the member are determined
    - We filter the RDF statements where the predicate matches the `fragmentationPath`.
    - If an optional regex is provided through the `fragmenterSubjectFilter` property, we filter on subjects that match this regex.
    - We select all the objects that pass the above filters.
2. The fragment of the member is determined. For each unit of time starting with year and ending with the chosen granularity from `maxGranularity` we do the following:
    - We take the value of this unit of time from the fragmentationObject. eg: the value of month for `2023-03-02T06:30:40` is `03`.
    - We check if the previous fragment has a child fragment with this value for the unit of time. (In the case of year, the previous fragment is the root fragment.)
    - If no such fragment exists, a new one is created.
3. The member is added to the last fragment.
4. When the member could not be added to any bucket (ex. the fragmentation property is missing or not valid), then the member will be added to a default bucket `year=unknown`.

## Example

Example properties:

  ```turtle
  @prefix tree: <https://w3id.org/tree#> .
  
  tree:fragmentationStrategy [
        a tree:HierarchicalTimeBasedFragmentation ;
        tree:maxGranularity "day" ;
        tree:fragmentationPath <http://www.w3.org/ns/prov#generatedAtTime> ;
    ] .
  ```
