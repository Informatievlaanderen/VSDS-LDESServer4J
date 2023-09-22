# Mongock Changeset 10

## Reason of this changeset
Due to an inconsistency in the code base, entity EventStream had a collection with the name eventstreams, 
while all the other collections had a singular collection name. This is now fixed in this change set


## Required config
Following configuration is needed to apply this changeset.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10
```

## Consequences of this changeset
* EventStream
  * Rename eventstreams to eventstream