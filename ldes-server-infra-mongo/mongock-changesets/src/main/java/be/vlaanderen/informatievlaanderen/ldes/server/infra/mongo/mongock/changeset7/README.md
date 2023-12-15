# Mongock Changeset 7

## Consequences of Changeset

* [Adds new collections for configuring the server](#new-collections)
* [Fills the new collections based on legacy config](#migration-of-configuration-from-yaml-to-new-mongo-collections)
* [Maps the fragmentation names to their new naming](#mapping-to-new-fragmentation-names)
* [Limitations](#limitations)

### New collections
This changeset adds the following collections:
- eventstreams
- view
- shacl_shape

These collections are used for configuration in server V1.

### Migration of configuration from yaml to new mongo collections

The mongock migration uses V0 yaml configuration the new mongo collections.
An example of this yaml config is included below.

### Mapping to new fragmentation names

The following fragmentations were renamed:
    pagination => PaginationFragmentation
    geospatial => GeospatialFragmentation

When the yaml config includes legacy naming (pagination, geospatial, etc.), these will be automatically converted
to the new naming according to the above table.

### Config needed

The following configuration is needed to apply this changeset.
```
mongock:
  migration-scan-package:
    - be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7
```

example format expected in application.yml:
```yaml
collections:
  - host-name: "http://localhost:8080"
    member-type: "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"
    timestamp-path: "http://www.w3.org/ns/prov#generatedAtTime"
    version-of: "http://purl.org/dc/terms/isVersionOf"
    collection-name: "parcels"
    views:
      - name: "by-page"
        fragmentations:
          - name: "pagination"
            config:
              memberLimit: 100
  - host-name: "http://localhost:8080"
    collection-name: "building-units"
    member-type: "https://data.vlaanderen.be/ns/gebouw#Gebouweenheid"
    timestamp-path: "http://www.w3.org/ns/prov#generatedAtTime"
    views:
      - name: "by-other-page"
        fragmentations:
          - name: "pagination"
            config:
              memberLimit: 1
      - name: "by-page"
        fragmentations:
          - name: "pagination"
            config:
              memberLimit: 5
```

### Limitations

New collections are only added when they do not yet exist or are still empty in the database.
If a collection already exists with data, the data from the yaml file will be ignored for this collection.