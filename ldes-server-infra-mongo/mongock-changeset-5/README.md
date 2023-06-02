# Mongock Changeset 5

The following configuration is needed to apply this changeset.

This changeset introduces a new collection with member information for the server v2 ingest module.
The legacy member changeset is not yet dropped as other new server v2 modules may require data from this collection
as well.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset5
```

Consequences of Changeset:
* Introduced collection `ingest_ldesmember`
