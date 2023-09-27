# Mongock Changeset 11


## Reason of this changeset
This changeset updates the FragmentEntity and renames the attribute `numberOfMembers` to `nrOfMembersAdded`
to better reflect its actual meaning.


## Required config
The following configuration is needed to apply this changeset.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset11
```

Consequences of Changeset:
* Renaming of `numberOfMembers` to `nrOfMembersAdded` in the LdesFragment
