# Mongock Changeset 3

The following configuration is needed to apply this changeset.

This changeset relies on the ldes configuration to assign members and fragments to a collection and impose an order on the members. When more than one collection is configured,
the migration will use `UNDEFINED_COLLECTION` as collectionName. The reason why it uses this value instead of `null`, is because the collectionName is added as prefix to ids and properties. Adding `null` might lead to unexpected behaviour. Using the value `UNDEFINED_COLLECTION`, it will be easier to adapt or rollback if needed.
When only one collection is configured, the properties of this collection will be used.

```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3
```

Consequences of Changeset 3:
* LdesMembers:
  * Added field `collectionName`
  * Added field `sequenceNr`
* LdesFragment:
  * Added field `collectionName`
* Introduced collection `member_sequence` (MemberSequenceEntity)
