---
layout: default
title: Fragment Compaction
nav_order: 1
---

# Fragment Compaction

To prevent that clients need to read fragments which are almost empty, the system foresees an algorithm to merge such fragments together so that fragment size stays as optimal as possible.  
This algorithm is called compaction and allows the server to merge immutable fragments that are underutilized (i.e. there are fewer members in the fragment than indicated in the [`tree:pageSize`](../_configuration/view) object of the view).

Merging the fragments will result in a new fragment and the members and relations of the compacted fragments will be "copied" to the new fragment.  
- Clients who haven't consumed the compacted fragments yet will be directed towards the new fragment, skipping the smaller, compacted fragments.
- Clients who are in the process of consuming the compacted fragments will be able to continue consumption of the compacted fragments for a limited, configurable, amount of time.   The default value for conserving the compacted fragments is 7 days (`PD7`), unless configured otherwise.


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


Compaction wil run at configurable moments, so that the optimal time can be determined and impact on performance can be limited.  

Configuration parameters of the compaction algorithm can be found on the [LDES Server Config](../how-to-run#ldes-server-config) section of the _How to run_ page.
