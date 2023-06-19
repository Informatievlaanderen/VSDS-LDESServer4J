# Mongock Changeset 6

The following configuration is needed to apply this changeset.

This changeset updates the LdesFragmentEntity and removes the attributes softDeleted and immutableTimestamp from this entity. If for some reason the changeset fails, the softDeleted attribute is set to `false` and the immutableTimestamp is set to the current time `LocalDateTime.now()` (in case the fragment is immutable) or `null` (in case the fragment is mutable).

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset6
```

Consequences of Changeset:
* Deletion of `softDeleted` and `immutableTimestamp` attributes of LdesFragment
