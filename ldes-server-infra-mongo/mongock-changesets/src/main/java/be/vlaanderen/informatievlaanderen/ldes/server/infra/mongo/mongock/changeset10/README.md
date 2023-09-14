# Mongock Changeset 10

The following configuration is needed to apply this changeset.

This changeset updates the FragmentEntity and renames the attribute `numberOfMembers` to `nrOfMembersAdded`
to better reflect its actual meaning.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10
```

Consequences of Changeset:
* Renaming of `numberOfMembers` to `nrOfMembersAdded` in the LdesFragment
