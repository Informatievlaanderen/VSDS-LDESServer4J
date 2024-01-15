# Mongock Changeset 14

## Reason of this changeset
We remove the field memberType from the evenstream collection because it is no longer used.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset14
```

Consequences of Changeset:
* eventstream:
  * Removed field memberType