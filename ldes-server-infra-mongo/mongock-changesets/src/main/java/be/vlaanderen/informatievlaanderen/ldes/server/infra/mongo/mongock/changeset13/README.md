# Mongock Changeset 13

## Reason of this changeset
With performance in mind, we reexamined all the indices we currently have on our database. Several of which were found to be redundant of insufficient.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset13
```

Consequences of Changeset:
* fragmentation_fragments:
  * Removed index on `root` and `immutable`
* fetch_allocation:
  * Removed index on `viewName` and `memberId`
* retention_member_properties:
  * Removed index on `versionOf` and `timestamp`