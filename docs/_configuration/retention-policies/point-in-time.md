---
layout: default
title: Point in Time Retention
nav_order: 1
parent: Retention Policies
---

# Point in Time Retention Policy
[https://w3id.org/ldes#PointInTimePolicy]()

As one of the most basic retention policies, the Point In Time Retention Policy will 
drop any members that have their `ldes:timestampPath` value before the set point in time value.


````mermaid
gantt
    title Point in Time Retention (Point in Time: 11-11-2023)
    dateFormat  YYYY-MM-DD
    todayMarker off
    
    Original Stream        :active, 2023-10-27, 45d
    Point In Time          :crit, milestone, 2023-11-11, 0d
    Stream After Retention :active, 2023-11-11, 30d
````

## Example

  ```turtle
  @prefix ldes: <https://w3id.org/ldes#> .
  @prefix tree: <https://w3id.org/tree#>.

  <view1> a tree:Node ;
    tree:viewDescription [
      a tree:ViewDescription ;
      ldes:retentionPolicy [
        a ldes:PointInTimePolicy ;
        <https://w3id.org/ldes#pointInTime>
          "2023-04-12T00:00:00"^^<http://www.w3.org/2001/XMLSchema#dateTime> ;
      ] ;
    ] .
  ```