# Mongock Changeset 15

## Reason of this changeset

As from now on, state objects can be ingested in the server, therefor some changes were required in the db to support this.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15
```

Consequences of Changeset:

* eventstream:
    * Add field versionCreationEnabled with the default value false, indicating the event stream ingest version objects
* ingest_ldesmember
    * Added fields:
        * versionOf, extracted from the saved model
        * timestamp, extracted from the saved model
        * transactionId, generated at random, as each member was a transaction on its own