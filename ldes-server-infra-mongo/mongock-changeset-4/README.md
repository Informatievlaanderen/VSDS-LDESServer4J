# Mongock Changeset 4

This changeset updates the member id's by adding the collectionName as prefix.
The prefix is separated by a '/'.
This change makes memberIds unique per collection, allowing duplicate memberId's between different collections.

The following configuration is needed to apply this changeset.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset4
```

Consequences of Changeset 4:
* LdesMembers:
  * Changed field `_id`
