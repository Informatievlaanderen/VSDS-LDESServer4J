---
layout: default
title: Version Based Retention
nav_order: 3
parent: Retention Policies
---

# Version Based Retention Policy
[https://w3id.org/ldes#LatestVersionSubset]()

To keep the Event Stream clean with less history, the Version Based Retention Policy 
allows to only keep a certain amount of versions of a state object (referenced through `ldes:versionOfPath`).

The amount of version to retain can be set as a number (higher than 0).

## Example 

```turtle
  @prefix ldes: <https://w3id.org/ldes#> .
  @prefix tree: <https://w3id.org/tree#>.

  <view1> a tree:Node ;
    tree:viewDescription [
      a tree:ViewDescription ;
      ldes:retentionPolicy [
        a ldes:LatestVersionSubset ;
        tree:amount 2 ;
      ] ;
    ] .
  ```