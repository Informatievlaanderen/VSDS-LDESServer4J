# Mongock Changeset 16

## Reason of this changeset

As from now on, an eventSource is used to determine which members should be deleted instead of the retention on views.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset16
```

Consequences of Changeset:

* eventstream:
    * Add field retentionPolicies
* ingest_ldesmember
    * Add field isInEventSource
* retention_member_properties
    * Add field isInEventSource