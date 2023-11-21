---
layout: default
title: Fragment Compaction
nav_order: 1
---

# Fragment Compaction

Compaction is a process that allows the server to merge immutable fragments that are underutilized (i.e. there are fewer members in the fragment than indicated in the `pageSize` of the view).
Merging the fragments will result in a new fragment and the members and relations of the compacted fragments will be "copied" to the new fragment.
This process runs entirely in the background. By default, the fragments that have been compacted will remain available for 7 days, `PD7`, but it can be configured differently. This to make sure that clients who are in the process of consuming the fragments have the time to continue consumption and get to the end of the Event Stream. When the period expires, the compacted fragments will be deleted.


To configure this interval, please refer to the [Configuration Page.](../../how-to-run#ldes-server-config)

````mermaid
%%{init: { 'gitGraph': {'mainBranchName': 'stream'}}}%%
gitGraph
    commit id: "Fragment 1: 100%"
    commit id: "Fragment 2: 100%"
    branch compaction
    checkout stream
    commit id: "Fragment 3: 25%" type: REVERSE
    commit id: "Fragment 4: 25%" type: REVERSE
    commit id: "Fragment 5: 25%" type: REVERSE
    checkout compaction
    commit id: "Fragment 3/5: 75%" type: REVERSE
    checkout stream
    merge compaction tag: "compacted Stream" type: HIGHLIGHT
    commit id: "Fragment 6: 75% (Open)"
````
