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
