# Mongock Changeset 1

Following configuration is needed to apply this changeset.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset1
```

Consequences of Changeset 1:
* LdesMembers:
  * Added field `TreeNodeReferences`
  * Updated field `ldesMember` to `model`
* LdesFragments:
  * Added field `softDeleted`
  * Added field `parentId`
  * Added field `immutableTimestamp`
  * Aggregated field `members` (List) to `numberOfMembers` (Number)