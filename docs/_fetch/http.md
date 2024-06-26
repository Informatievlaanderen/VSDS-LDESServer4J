---
layout: default
title: Read Linked Data Event Streams With HTTP
nav_order: 1
---

# Read Linked Data Event Streams With HTTP

Although the Linked Data Event Streams are advised to read with an [LDES Client](./ldes-client),
an Event Stream can also be retrieved via HTTP.

When manually retrieving an LDES, we can make a distinction into 3 categories:

## Retrieving an Event Stream

By browsing to an Event Stream following the pattern `{hostname}/{event stream}`,
you are able to read the shape of the Event Stream, its views 
(along with its configured fragmentations) and configured DCAT information.

## Retrieving a View

By following the previously mentioned views or by following the pattern `{hostname}/{event stream}/{view}`,
the view page will be shown. This contains information about how many members are in this Event Stream,
the configured DCAT information and the `tree:Relation` that points to the root fragment.

## Retrieving a fragment

Finally, by following the root fragment from a view or by following the pattern 
`{hostname}/{event stream}/{view}?{fragmentation specific parameters}`, the fragment page will be shown.
Depending on whether any fragmentations are defined, this either contains a partitioned fragment page
or one or multiple`tree:Relation` that point to partitioned fragment pages.

These partitioned fragments contain the actual members.

### Retrieving a fragment in a streaming way

When retrieving a fragment, it is possible to receive the fragment page in a streaming way by specifying the accept type as `text/event-stream`.
The fragment will then always be converted to the `application/rdf+protobuf` format.
Because server-side events are used we encode the data to base64.

When retrieving a fragment in this way, not all the data will be received at once.

1. First the statements relating to the fragment itself and its relations to other fragments will be send. This event will have the name: `metadata`.
2. Then all members will be send one by one. These events will have the name: `member`.

Every piece of the fragment data is wrapped in the data part of a Server-side event.

This method is useful when retrieving large fragments because you do not have to wait until every member is fetched and the entire fragment is constructed to start processing the data.
When consuming a fragment in this way, it is important to recognise that an error can still occur after receiving an 200 OK response from the server.
When such an error occurs, the error message will be send in a Server-side event named `error`.
