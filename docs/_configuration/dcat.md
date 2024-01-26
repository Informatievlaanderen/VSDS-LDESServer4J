---
layout: default
title: Configuring Data Catalog Vocabulary (DCAT)
nav_order: 5
has_toc: true
---

# Configuring Data Catalog Vocabulary (DCAT)

DCAT is an RDF vocabulary designed to facilitate interoperability between data catalogs published on the Web. This
document defines the schema and provides examples for its use.

DCAT enables a publisher to describe datasets and data services in a catalog using a standard model and vocabulary that
facilitates the consumption and aggregation of metadata from multiple catalogs. This can increase the discoverability of
datasets and data services. It also makes it possible to have a decentralized approach to publishing data catalogs and
makes federated search for datasets across catalogs in multiple sites possible using the same query mechanism and
structure. Aggregated DCAT metadata can serve as a manifest file as part of the digital preservation process.

For more info on DCAT, visit the [DCAT publication](https://www.w3.org/TR/vocab-dcat-2/)

There are DCAT templates available for two supported profiles on
the [GitHub repository](https://github.com/Informatievlaanderen/VSDS-LDESServer4J/tree/main/templates/dcat)

## Validity of the configured DCAT

The validity of the configured DCAT can be checked, but then first a DCAT shacl shape is required. This shacl shape can
be configured with the following yaml:

```yaml
ldes-server:
  dcat-shape: <file-uri>
```

When this is configured, two different endpoints can be polled to check the validity:

1. the DCAT endpoint

```shell
curl 'localhost:8080/admin/api/v1/dcat'
```

This endpoint returns a 200 status code together with the configured DCAT when it's valid, and it returns 500 together
with a validation report when it's not valid.

2. the health endpoint(s)

To poll this endpoint successfully, additional config is required. An example config will be provided here, but more
info on how to configure the health endpoints can be
found [here](https://github.com/Informatievlaanderen/VSDS-LDESServer4J#health-and-info).

```yaml
management:
  endpoints:
    web:
      exposure:
        include:
          - health
  endpoint:
    health:
      status:
        http-mapping:
          invalid: 500
          unknown: 500
      group:
        dcat-validity:
          show-components: always
          show-details: always
          include: dcat
```

This config will first enable the `/actuator/health` endpoints. \
Secondly, a mapping is provided, so when an `UNKNOWN`
or `INVALID` status is returned, a http status 500 is associated with it. \
At last, a group is defined, this ensures that only the details of the DCAT and its validity are exposed and the rest of
the health info is still
hidden.

With this config, the following health endpoint can be polled
```shell
curl http://localhost:8080/actuator/health/dcat-validity
```
If the configured DCAT is valid, a 200 response code with the following response body will be returned
```json
{
  "status": "UP",
  "components": {
    "dcat": {
      "status": "UP"
    }
  }
}
```
If not, a 500 response code will be returned with the following response body:
```json
{
  "status": "UNKNOWN",
  "components": {
    "dcat": {
      "status": "INVALID",
      "details": {
        "error": "be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException: Shacl validation failed: \n\nnull"
      }
    }
  }
}
```

{: .note }
All DCAT API endpoints can be found on the Swagger UI endpoint configured in [the run guide.](../how-to-run)