# Mongock Changeset 9

As part of the Isolation of the Fragmentation module, the fragment table now has a fragmentation prefix.

Following configuration is needed to apply this changeset.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset9
```

Consequences of Changeset:
* LdesFragment -> Fragment:
    * Rename ldesfragment to fragmentation_fragment