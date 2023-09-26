# Mongock Changeset 10

The following configuration is needed to apply this changeset.

This changeset updates the TreeRelation in FragmentEntity so it uses LdesFragmentIdentifier instead of a string for its treenode.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10
```

Consequences of Changeset:
* TreeRelation now uses LdesFragmentIdentifier instead of a string
