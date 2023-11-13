---
layout: default
title: Ingest Members With HTTP
nav_order: 0
---

# Ingest Members With HTTP

An Event Stream without its members is nothing. Therefor, new members can be ingested via 
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

Every Event Stream contains a shape that mentions a targetClass. 
This is the type should be the same as the `rdf:type` of the root object you try to ingest.
Otherwise it will be rejected