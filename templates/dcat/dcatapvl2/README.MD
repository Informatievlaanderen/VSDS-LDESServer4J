# DCAT-AP-VL 2.0
This template is based on the [DCAT-AP-VL 2.0 application profile](https://data.vlaanderen.be/doc/applicatieprofiel/DCAT-AP-VL) and is in conformance with its [SHACL template](https://data.vlaanderen.be/doc/applicatieprofiel/DCAT-AP-VL#shacl).
The template can be found [here](dcat-template.ttl)

The below resources are present on the template

## dcat:Catalog
This resource concerns the organisation as a whole. The metadata should only contain this resource once, and
it should always be the same.

## dcat:DataSet
This represents the [LDES stream](https://semiceu.github.io/LinkedDataEventStreams/) itself and the underlying data. A separate dcat:Dataset should be included for every different LDES Stream.

Note that a list of all dcat:Dataset should be present on the dcat:Catalog resource on the dcat:dataset statement.

## dcat:DataService
This represents a [view](https://treecg.github.io/specification/#view). A separate dcat:DataService should be included for every different view.

Note that a list of all dcat:DataService should be present on the dcat:Catalog resource on the dcat:service statement.
