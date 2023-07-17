# Mongock Changeset 9

As part of the Isolation of the Fragmentation module, the fragment table now has a fragmentation prefix.

As part of the Isolation of the Retention module a new table retention_member_properties has been created.
This new table contains the part of the properties of the ldesmember table relevant to retention.

Following configuration is needed to apply this changeset.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset9
```

Consequences of Changeset:
* LdesFragment -> Fragment:
    * Rename ldesfragment to fragmentation_fragment
* add table retention_member_properties
        