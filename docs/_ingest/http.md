---
layout: default
title: Ingest Members With HTTP
nav_order: 0
---

# Ingest Members With HTTP

An Event Stream without its members is nothing. Therefore, new members can be ingested via
a `POST` HTTP Endpoint. This endpoint follows the following pattern:
`{ldes server hostname}/{event-stream}`.

Note that this event stream should already be configured.
(Read [Configuring a Event Stream](../configuration/event-stream) for more details)

## Accepted formats

The LDES Server accepts every RDF type supported by Apache JENA.
When sending the RDF data, make sure this is specified in the `Content-Type` header.
The most common types to use are `application/n-quads`, `text/turtle` and `application/ld+json`.

For more details, please refer to the Swagger API under the `base` definition.

## Version objects, state objects and version creation

Currently, an event stream can ingest version objects by default. A version object describes the state of a specific
version of a resource at a specific timestamp.

However, an event stream can be configured in such a way that it can ingest state objects. Once the event stream and
its fragments are retrieved, the members will be presented as version objects. A state object describes the latest state
of a resource. When such an object is ingested, the server stores the RDF model as is, the timestamp of ingestion and
determines the subject of that member.

When the members of such an event stream are fetched, the stored RDF model is enriched with the version object
properties,
which includes:

- the named subject node, which is the versionOf, will be replaced with the member id, which has the following
  structure: `{versionOf}/{timestamp}`
- the following statements will be added:
    * `<{memberId}> <{timestamp-path-of-the-ldes}> "{timestamp}""^^<http://www.w3.org/2001/XMLSchema#dateTime>`
    * `<{memberId}> <{version-of-path-of-the-ldes}> <versionOf>`

## Bulk ingestion

When the event stream is configured to ingest state objects, a fun side effect is enabled, namely bulk ingest. This is a
result of the extraction algorithm used to extract all the state objects out of the ingested RDF model. This algorithm
searches for all the named subject nodes and then searches for all the nested statements that are related to each named
subject. All the statements of each subject and its nested statements will be put together in an RDF model resulting in
one or many members.

> **CAUTION:** when the ingestion fails for one of the members, e.g. due to a validation violation,
> the ingestion for **all** the members will fail and none of them will be stored.

## Member Conformity

Only one member can be ingested at a time. Bulk ingest is (not yet) supported.
Every model that is sent for ingestion, should contain exactly one named node.
Otherwise it will be rejected.

## Duplicate Members

When a member is ingested normally, it is saved in the server and a 201 ACCEPTED status is returned.
Sometimes a member with the same ID as an existing member can be send to the ingest endpoint.
In this case, the second member will be ignored, a warning will be logged and a 200 OK status will be returned.