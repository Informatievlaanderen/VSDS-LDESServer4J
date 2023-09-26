# Mongock Changeset 10


## Reason of this changeset
This changeset updates the TreeRelation in FragmentEntity, so it uses LdesFragmentIdentifier instead of a string for its treenode.


## Required config
The following configuration is needed to apply this changeset.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10
```

## Consequences of Changeset:
* TreeRelation now uses LdesFragmentIdentifier instead of a string
