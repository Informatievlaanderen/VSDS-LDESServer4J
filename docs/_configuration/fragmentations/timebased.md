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
        tree:linearTimeCachingEnabled { Optional: indicates if fragments should be cached by linear time (true/false) } ;
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

### Linear time caching
This configuration which is off by default makes it possible to activate intelligent caching for time based data.
When you have a data with timestamps that keep increasing and are always current or newer it possible to improve 
the caching. **We always assume that future members will have future timestamps.**

**For example**
timestamp: 21/02/2024

Will result in fragments:
- root
- year=2024
- year=2024&month=02
- year=2024&month=02&day=21

We will assume that no new year will be added to the root until 31/12/2024.
We will assume that no new month will be added to year=2024 until 29/02/2024.
We will assume that no new day will be added to year=2024&month=02 until 21/02/2024 23:59:59.

When a new day is added later, for example 22/02/2024. All other days and all its pages in year=2024&month=02 will 
become immutable. We always assume that future members will have future timestamps.

The above logic makes the server a lot more efficient for this kind of timebased data. Clients won't have to 
keep track of an increasing number of open fragments.

This optional feature is disabled by default and needs to be activated 
by adding  `tree:linearTimeCachingEnabled true ;` to the config.

**Historical data seeding**
This feature will still work when you seed a new server with historical data as long as you ingest the data 
in chronological order. This is because fragments do not become immutable until a fragment further in time is added.

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
