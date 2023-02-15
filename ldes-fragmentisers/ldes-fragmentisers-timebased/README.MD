# Timebased fragmentation
This fragmentation is currently no longer supported as it is a predecessor of [the pagination fragmentiser](../ldes-fragmentisers-pagination).

This fragmentiser will create an initial fragment with the current timestamp when processing a member.
Members are added to the fragment until the member limit is reached. When the fragment member limit is reached, a
next fragment is created with a new current timestamp.

This fragmentiser follows the algorithm of pagination but without the semantics.