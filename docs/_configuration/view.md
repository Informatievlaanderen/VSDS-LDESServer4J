---
layout: default
title: Configuring a new View
nav_order: 2
---

# Configuring a new View for an Event Stream

After having created an Event Stream, a view needs to be defined to be able to retrieve the data.

This can be done through the Admin API at the `/admin/api/v1/eventstreams/{event stream}/views` endpoint.

A view config needs to have the following structure:

* A `tree:viewDescription` object with its subject referring to the event stream object
  * a `tree:FragmentationStrategy` object that contains an ordered rdf list of fragmentations.
  * a `ldes:retentionPolicy` object that contains a set of retention policies. When no retention policies are required, this is omitted.
  * a `tree:pageSize` object that marks how many members should be partitioned per fragment.

For more info, visit the [Swagger API documentation.](./admin-api)

### Fragmentations

To provide a more structured overview of the data, a fragmentation list can be defined.

For a more detailed explanation on fragmentation, together with all the available options, 
visit the [Fragmentations Subsection](./fragmentations).

### Retention Policies

To reduce the amount of historical data kept in the LDES Server, one can configure a set of retention policies.

For a more detailed explanation on retention policies, together with all the available options,
visit the [Retention Policies Subsection](./retention-policies).

## Example

````turtle
@prefix ldes: <https://w3id.org/ldes#> .
@prefix tree: <https://w3id.org/tree#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix server: <http://localhost:8080/generic-eventstream/> .

server:view-name tree:viewDescription [
    a tree:fragmentationStrategy;
    tree:fragmentationStrategy  () ;
    tree:pageSize "10"^^<http://www.w3.org/2001/XMLSchema#int> ;
] .
````
