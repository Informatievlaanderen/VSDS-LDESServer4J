# Timebased fragmentation [DEPRECATED]
This fragmentation is currently no longer supported as it is a predecessor of [the pagination fragmentiser](../ldes-fragmentisers-pagination).
Please use pagination instead.

### Algorithm
This fragmentiser will create an initial fragment with the current timestamp when processing a member.
Members are added to the fragment until the member limit is reached. When the fragment member limit is reached, a
next fragment is created with a new current timestamp.


### Reasons for deprecating this fragmentiser:
1. This fragmentiser follows the algorithm of pagination but without the semantics.
2. For a correct timebased fragmentation, members of the fragment should be checked and their value 
for a given property should be used to create the correct relations. This is not the case, and there is currently no demand to have this implemented.
