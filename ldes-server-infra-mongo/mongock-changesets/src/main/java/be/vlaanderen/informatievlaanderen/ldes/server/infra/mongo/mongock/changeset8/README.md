# Mongock Changeset 8

In changeset 6 we deleted

Following configuration is needed to apply this changeset.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset8
```

Consequences of Changeset:
* LdesFragments:
  * Removed index on `softDeleted`