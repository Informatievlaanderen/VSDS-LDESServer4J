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
the configured DCAT information and the `tree:Relation` that point to the root fragment.

## Retrieving a fragment

Finally, by following the root fragment from a view or by following the pattern 
`{hostname}/{event stream}/{view}?{fragmentation specific parameters}`, the fragment page will be shown.
Depending on wether any fragmentations are defined, this either contains a partitioned fragment page
or one or multiple`tree:Relation` that point to partitioned fragment pages.

These partitioned fragments contain the actual members.