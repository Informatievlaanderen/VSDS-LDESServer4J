# Mongock Changeset 1

Following configuration is needed to apply this changeset.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset2
```

Consequences of Changeset 2:
* LdesMembers:
  * Added field `versionOf`
  * Added field `timestamp`
