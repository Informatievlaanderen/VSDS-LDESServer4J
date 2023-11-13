---
layout: default
title: Timebased Retention
nav_order: 2
parent: Retention Policies
---

# Timebased Retention Policy
[https://w3id.org/ldes#DurationAgoPolicy]()

Similar to the [Point in Time Retention Policy](./point-in-time), 
the Timebased Retention Policy will filter out members based on their `ldes:timestampPath`.
The difference between the previous retention policy is that the Timebased one works with a 
sliding window, rather than a hard-set value.

The sliding window can be defined with a [ISO 8601 Temporal Duration](https://tc39.es/proposal-temporal/docs/duration.html).
Any members' `ldes:timestampPath` that falls outside of this range will be removed.

```mermaid
gantt
    title Timebased Retention (Range: P2D)
    dateFormat YYYY-MM-DD
    todayMarker off

    section Day 1
        Current Day: crit, milestone, 2023-11-11, 0d
        Original Stream: active, 2023-11-08, 3d
        Sliding Window (Current Day -2 days): 2023-11-9, 2d
        Stream After Retention Day 1: active, 2023-11-9, 2d

    section Day 2
        Current Day: crit, milestone, 2023-11-12, 0d
        Original Stream: active, 2023-11-9, 3d
        Sliding Window (Current Day -2 days): 2023-11-10, 2d
        Stream After Retention Day 2: active, 2023-11-10, 2d
```

## Example 

```turtle
  @prefix ldes: <https://w3id.org/ldes#> .
  @prefix tree: <https://w3id.org/tree#>.

  <view1> a tree:Node ;
    tree:viewDescription [
      a tree:ViewDescription ;
      ldes:retentionPolicy [
        a ldes:DurationAgoPolicy ;
        tree:value "PT10M"^^<http://www.w3.org/2001/XMLSchema#duration> ;
      ] ;
    ] .
  ```