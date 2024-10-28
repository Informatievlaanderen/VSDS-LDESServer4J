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
  structure: `{versionOf}{versionDelimiter}{timestamp}`. By default, the `versionDelimiter` is `/`, but this can be
  configured with any string, which then results in the following structure for the named subject node:
  `{versionOf}/{timestamp}`.
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

Every member should conform to certain conditions, depending on the event stream on which they are ingested.

### Named nodes

Every named node is viewed as a member.
Having a named node that is not intended as a member to be ingested on the collection can lead to several validation
errors.
Most commonly this will lead to a validation error stating the timestamp path and version-of path are missing, depending
on the data itself.

If by chance it conforms to the all following validation, it will be treated as any normal member.
To prevent such cases,
additional [shacl validation can be configured](../configuration/event-stream#configuring-a-shacl-shape) on the event
stream.

### Named Graphs

Named graphs are not supported.
When a named graph is present, the model is rejected.

### Shared or Loose Blank Nodes

All blank nodes should be referenced by exactly 1 other subject.
When a blank node is present that is not the object of a statement or the object of 2 or more statements with different
subjects, the ingested model is rejected.

This also means that the root node of every member should be a named node.

It is still possible for a single subject to reference the same blank node multiple times.

### Timestamp and Version Of Path

Every event stream defines the property where the timestamp and version of can be found on each member.
If the event stream has version creation NOT enabled, these properties should be present on each member.
If version creation is enabled, these properties should NOT be present on the members received.

The timestamp defined on the timestamp path should be of the type `<http://www.w3.org/2001/XMLSchema#dateTime>`.
The timestamp must have this datatype explicitly declared.
ex.
`<https://example.be/member/1> <http://www.w3.org/ns/prov#generatedAtTime> "1996-03-28T09:58:15.867Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .`

The object defined on the versionOf path must be a uri. This uri should represent the state object of which the member
is a version.
ex. `<https://example.be/member/1> <http://purl.org/dc/terms/isVersionOf> <https://example.be/member> .`

### Bulk Ingestion

Depending on if the event stream has version creation enabled, multiple members can be ingested with a single POST
request.
Later this will also be possible without version creation.
For now, a request will be rejected if multiple named nodes are found which are not referenced by any triple. (Without
version creation enabled)

## Duplicate Members

When a member is ingested normally, it is saved in the server and a 201 ACCEPTED status is returned.
Sometimes a member with the same ID as an existing member can be send to the ingest endpoint.
In this case, the second member will be ignored, a warning will be logged and a 200 OK status will be returned.