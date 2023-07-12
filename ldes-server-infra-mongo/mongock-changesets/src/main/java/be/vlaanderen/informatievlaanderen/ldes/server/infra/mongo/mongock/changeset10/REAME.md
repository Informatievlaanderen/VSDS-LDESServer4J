# Mongock Changeset 10

As part of the Isolation of the Retention module, the ldesmember table has been removed and replaced with member_properties.
This new table does not persist all the previous properties.

Following configuration is needed to apply this changeset.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10
```

Consequences of Changeset:
* renamed table ldesmember -> member_properties
* table member_properties no longer has the properties sequenceNr and model
* renamed property of table member_properties: treeNodeReferences -> views
        
