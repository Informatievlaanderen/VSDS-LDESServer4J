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

## Member Conformity

Only one member can be ingested at a time. Bulk ingest is (not yet) supported.
Every model that is sent for ingestion, should contain exactly one named node.
Otherwise it will be rejected.

## Duplicate Members

When a member is ingested normally, it is saved in the server and a 201 ACCEPTED status is returned.
Sometimes a member with the same ID as an existing member can be send to the ingest endpoint.
In this case, the second member will be ignored, a warning will be logged and a 200 OK status will be returned.