# Mongock Changeset 16

## Reason of this changeset

To increase performance, the members are now stored in the binary RDF format protobuf, instead of the string format N-Quads. 

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset16
```

Consequences of Changeset:

* ingest_ldesmember
    * Updated fields
      * model: now stored in protobuf, which is a binary dataformat