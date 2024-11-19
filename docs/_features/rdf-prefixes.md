---
layout: default
title: RDF Prefixes
nav_order: 3
---

# RDF Prefixes
{: .no_toc }

For some RDF formats, e.g. `text/turtle`, prefixes can be added to have a more clean and readable output. There are
several ways that prefixes will be added to the output of the LDES Server.

1. TOC
{:toc}

## Well known prefixes

Well known prefixes are some of the prefixes that are most frequent used in LDES and are hard coded in the LDES server
and can be found here:

| Prefix | URI                                         |
|--------|---------------------------------------------|
| foaf   | http://xmlns.com/foaf/0.1/                  |
| rdf    | http://www.w3.org/1999/02/22-rdf-syntax-ns# |
| rdfs   | http://www.w3.org/2000/01/rdf-schema#       |
| skos   | http://www.w3.org/2004/02/skos/core#        |
| owl    | http://www.w3.org/2002/07/owl#              |
| xsd    | http://www.w3.org/2001/XMLSchema#           |
| geo    | http://www.opengis.net/ont/geosparql#       |
| dcat   | http://www.w3.org/ns/dcat#                  |
| dct    | http://purl.org/dc/terms/                   |
| prov   | http://www.w3.org/ns/prov#                  |
| m8g    | http://data.europa.eu/m8g/                  |
| tree   | https://w3id.org/tree#                      |
| ldes   | https://w3id.org/ldes#                      |
| sh     | http://www.w3.org/ns/shacl#                 |
| shsh   | http://www.w3.org/ns/shacl-shacl#           |

## Configured prefixes

When needed, additional prefixes can be added to the LDES Server. This can come in handy for project specific prefixes.
To add these prefixes, a map can be added to the application properties as follows

```yaml
ldes-server:
  formatting:
    prefixes:
      prefix1: uri1
      prefix2: uri2
      prefix3: uri3
```

In the following example are two prefixes added to the LDES Server:
```yaml
ldes-server:
  formatting:
    prefixes:
      example: http://example.org/
      vsds-verkeersmetingen: http://data.vlaanderen.be/ns/verkeersmetingen#
```

## Event Stream and fragment specific prefixes

Per event stream and fragment, prefixes will be extracted and added to the output. Those will look something like this:

**Event stream**
- `collectionName: ${ldes-server.host-name}/{collectionName}`
 
**Tree node**
- `collectionName: ${ldes-server.host-name}/{collectionName}`
- `viewName: ${ldes-server.host-name}/{collectionName}/{viewName}`
