# Mongock Changeset 2

The following configuration is needed to apply this changeset.

This changeset relies on the ldes configuration to determine the member fields. When more than one collection is configured,
the migration does not know which version or timestamp to use. In this case it will remain empty.
When only one collection is configured, the properties of this collection will be used.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset2
```

Consequences of Changeset 2:
* LdesMembers:
  * Added field `versionOf`
  * Added field `timestamp`
