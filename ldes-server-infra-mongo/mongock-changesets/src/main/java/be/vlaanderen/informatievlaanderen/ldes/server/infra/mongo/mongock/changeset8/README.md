# Mongock Changeset 8

In changeset 6 we deleted the indexed field softDeleted but the index was never cleaned up.

Following configuration is needed to apply this changeset.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset8
```

Consequences of Changeset:
* LdesFragments:
  * Removed index on `softDeleted`