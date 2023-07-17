# Mongock Changeset 10

As part of the Isolation of the Retention module a new table retention_member_properties has been created.
This new table contains the part of the properties of the ldesmember table relevant to retention.

Following configuration is needed to apply this changeset.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10
```

Consequences of Changeset:
* add table retention_member_properties
        
