# Admin rest api

The api endpoints to configure different streams.
Currently, they have no impact on the working of the server, this has yet to be implemented.
The given description details the eventual purpose of the endpoint.


## Endpoints

| Endpoint                | url                                                                       | Method | Description                                                                               |
|-------------------------|---------------------------------------------------------------------------|--------|-------------------------------------------------------------------------------------------|
| **Get eventstreams**    | {server_url}/admin/api/v1/eventstreams                                    | GET    | Get the configuration of all eventstreams with their metadata if present                  |
| **Post eventstream**    | {server_url}/admin/api/v1/eventstreams                                    | POST   | Add a new eventstream to the server                                                       |
| **Get eventstream**     | {server_url}/admin/api/v1/eventstreams/{collection_name}                  | GET    | Get the configuration of an eventstream with the given name  with its metadata if present |
| **Delete eventstreams** | {server_url}/admin/api/v1/eventstreams/{collection_name}                  | DELETE | Remove an eventstream with the given name                                                 |
| **Get shape**           | {server_url}/admin/api/v1/eventstreams/{collection_name}/shape            | GET    | Get the SHACL shape the eventstream                                                       |
| **Put shape**           | {server_url}/admin/api/v1/eventstreams/{collection_name}/shape            | PUT    | Add a SHACL shape to an eventstream or change it if one is already present                |
| **Get views**           | {server_url}/admin/api/v1/eventstreams/{collection_name}/views            | GET    | Get all the views of the eventstream with their metadata if present                       |
| **Post view**           | {server_url}/admin/api/v1/eventstreams/{collection_name}/views            | POST   | Add a new view to the eventstream                                                         |
| **Get view**            | {server_url}/admin/api/v1/eventstreams/{collection_name}/views/{viewName} | GET    | Get the view with the given name of the chosen eventstream with its metadata if present   |
| **Delete view**         | {server_url}/admin/api/v1/eventstreams/{collection_name}/views/{viewName} | DELETE | Remove a view with the given name from the chosen eventstream                             |


## Input data

The body of any ``post`` or ``put`` request must contain a rdf model in the turtle format. Otherwise, a BAD REQUEST status is returned.
Every resource describing a stream, shape or view can exist solely of a name. The server will add its own prefix based on the hostname.

Ex. ``` <collectionName> a ldes:EventStream ; ``` with ``` "https://myldes" ``` as hostname will have the name: ``` http://myldes/collectionName ```


## LDES config SHACL shape validation

When a ``POST`` request is sent to add the config of the event streams, the SHACL shape of this config in the request
body will be validated by ``LdesConfigShaclValidator.validate``. Nothing will be returned if this input is valid,
otherwise a ``LdesShaclValidationException`` will be thrown with the validation report within.

Important to note is that contrary to other SHACL validators, 
a ``LdesShaclValidationException`` will also be thrown if the input data is not of the type the validator is expecting to receive.
This is a feature of the provided SHACL shapes, not of the validator itself.

The files with the required SHACL shapes can be found in [this folder](src/main/resources).

### LDES Event stream

The most important part for the event stream validation, is that a shape is required. The timestampPath and
versionOfPath are optional here. Extra fields/properties are prohibited in this validation, which means a new event
stream cannot have views already.

An example of a valid LDES Event stream can be found here:

```
@prefix ldes: <https://w3id.org/ldes#> .
@prefix dcterms: <http://purl.org/dc/terms/> .
@prefix tree: <https://w3id.org/tree#>.
@prefix sh:   <http://www.w3.org/ns/shacl#> .
@prefix example:   <http://example.org/> .


<collectionName> a ldes:EventStream ;
    ldes:timestampPath dcterms:created ;
    ldes:versionOfPath dcterms:isVersionOf ;
    example:memberType <https://exampleMembertType> ;
    tree:shape [
        sh:closed true;
        a sh:NodeShape ;
    ] .
```

### LDES SHACL shape

In the shape of the SHACL shape are none of the properties required to be provided, but no extra properties can be
provided then those that are included in the following example.

```
@prefix sh:   <http://www.w3.org/ns/shacl#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .

[] a sh:NodeShape;
  sh:targetClass <https://w3id.org/ldes#EventStream> ;
  sh:closed true;
  sh:ignoredProperties (rdf:type) ;
  sh:property [
    sh:class sh:NodeShape;
    sh:description "The schema all elements of the eventstream must conform to."@en;
    sh:maxCount 1;
    sh:minCount 1;
    sh:name "shape"@en;
    sh:path <https://w3id.org/tree#shape>
  ], [
    sh:nodeKind sh:IRI ;
    sh:description "The object property of the members that idicates how members relate to each other from the time perspective."@en;
    sh:maxCount 1;
    sh:name "timestampPath"@en;
    sh:path <https://w3id.org/ldes#timestampPath>
  ], [
    sh:nodeKind sh:IRI ;
    sh:description "The object property that indicates the object identifier in a version object."@en;
    sh:maxCount 1;
    sh:name "versionOfPath"@en;
    sh:path <https://w3id.org/ldes#versionOfPath>
  ] .
```

### LDES view
There is no SHACL shape for the LDES view provided yet. But the following example was used in the tests as a valid view
```
@prefix tree: <https://w3id.org/tree#> .
@prefix ldes: <https://w3id.org/ldes#> .
@prefix example: <http://example.org/> .

<view1> a ldes:View ;
  tree:viewDescription [
    example:fragmentationStrategy [
        a example:Fragmentation ;
        example:name "pagination";
        example:memberLimit "10" ;
    ]
] .
```

## DCAT endpoints

## Endpoints

| Endpoint                    | url                                                                             | Method | Description                                            |
|-----------------------------|---------------------------------------------------------------------------------|--------|--------------------------------------------------------|
| **Get server DCAT**         | {server_url}/admin/api/v1/dcat                                                  | GET    | Get the DCAT of the server, all eventstreams and views |
| **Post server DCAT**        | {server_url}/admin/api/v1/dcat                                                  | POST   | Add the DCAT of the server                             |
| **Put server DCAT**         | {server_url}/admin/api/v1/dcat/{catalogId}                                      | PUT    | Update the DCAT of the server                          |
| **Delete server DCAT**      | {server_url}/admin/api/v1/dcat/{catalogId}                                      | DELETE | Remove the DCAT of the server                          |
| **Post eventstream DCAT**   | {server_url}/admin/api/v1/eventstreams/{collection_name}/dcat                   | POST   | Add DCAT to a given eventstream                        |
| **Put eventstream DCAT**    | {server_url}/admin/api/v1/eventstreams/{collection_name}/dcat                   | PUT    | Update the DCAT of a given eventstream                 |
| **Delete eventstream DCAT** | {server_url}/admin/api/v1/eventstreams/{collection_name}/dcat                   | DELETE | Remove the DCAT of a given eventstream                 |
| **Post view DCAT**          | {server_url}/admin/api/v1/eventstreams/{collection_name}/views/{viewName}/dcat  | POST   | Add a DCAT to a given view                             |
| **Put view DCAT**           | {server_url}/admin/api/v1/eventstreams/{collection_name}/views/{viewName}/dcat  | PUT    | Update the DCAT of a given view                        |
| **Delete view DCAT**        | {server_url}/admin/api/v1/eventstreams/{collection_name}/views/{viewName}/dcat  | DELETE | Remove the DCAT of a given view                        |

To DCAT data of an eventstream or view can be retrieved on their normal [GET endpoints](#endpoints) together with their configuration.

### Server DCAT

The DCAT of a LDES server is of the type http://www.w3.org/ns/dcat#Catalog.
A valid DCAT must contain a blank node of this type and contain only properties of this type, excluding relations to nodes of the type dcat:Dataset or dcat:DataService.

  ```ttl
  @prefix dct:   <http://purl.org/dc/terms/> .
  @prefix dcat:  <http://www.w3.org/ns/dcat#> .

  [] a dcat:Catalog ;
    dct:title "My LDES'es"@en ;
    dct:description "All LDES'es from publiser X"@en .
  ```

### Eventstream DCAT

The DCAT of an eventstream is of the type http://www.w3.org/ns/dcat#Dataset.
A valid DCAT must contain a blank node of this type and contain only properties of this type, excluding relations to nodes of the type dcat:Catalog or dcat:DataService.

  ```ttl
  @prefix dct:   <http://purl.org/dc/terms/> .
  @prefix dcat:  <http://www.w3.org/ns/dcat#> .
  @prefix foaf:  <http://xmlns.com/foaf/0.1/> .
  @prefix org:   <http://www.w3.org/ns/org#> .
  @prefix legal: <http://www.w3.org/ns/legal#> .
  @prefix m8g:   <http://data.europa.eu/m8g/> .
  @prefix locn:  <http://www.w3.org/ns/locn#> .

  [] a dcat:Dataset ;
    dct:title "My LDES"@en ;
    dct:title "Mijn LDES"@nl ;
    dct:description "LDES for my data collection"@en ;
    dct:description "LDES vir my data-insameling"@af ;
    dct:creator <http://sample.org/company/MyDataOwner> .

  <http://sample.org/company/MyDataOwner> a legal:LegalEntity ;
    foaf:name "Data Company" ;
    legal:legalName "Data Company BV" ;
    m8g:registeredAddress [
      a locn:Address ;
      locn:fullAddress "My full address here"
    ] ;
    m8g:contactPoint [
      a m8g:ContactPoint ;
      m8g:hasEmail "info@data-company.com"
    ] .
  ```

### View DCAT

The DCAT of an eventstream is of the type http://www.w3.org/ns/dcat#DataService.
A valid DCAT must contain a blank node of this type and contain only properties of this type, excluding relations to nodes of the type dcat:Catalog or dcat:Dataset.

  ```ttl
  @prefix dct:   <http://purl.org/dc/terms/> .
  @prefix dcat:  <http://www.w3.org/ns/dcat#> .

  [] a dcat:DataService ;
    dct:title "My geo-spatial view"@en ;
    dct:description "Geospatial fragmentation for my LDES"@en ;
    dct:license [
      a dct:LicenseDocument ;
    ] .
  ```
