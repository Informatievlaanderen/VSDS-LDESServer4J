# Admin rest api

The api endpoints to configure different streams.
Currently, they have no impact on the working of the server, this has yet to be implemented.
The given description details the eventual purpose of the endpoint.


## Endpoints

| Endpoint                | url                                                                       | Method | Description                                                                 |
|-------------------------|---------------------------------------------------------------------------|--------|-----------------------------------------------------------------------------|
| **Get eventstreams**    | {server_url}/admin/api/v1/eventstreams                                    | GET    | Get the configuration of all eventstreams                                   |
| **Put eventstream**     | {server_url}/admin/api/v1/eventstreams                                    | PUT    | Add a new eventstream to the server or update an existing one               |
| **Get eventstream**     | {server_url}/admin/api/v1/eventstreams/{collection_name}                  | GET    | Get the configuration of an eventstream with the given name                 |
| **Delete eventstreams** | {server_url}/admin/api/v1/eventstreams/{collection_name}                  | DELETE | Remove an eventstream with the given name                                   |
| **Get shape**           | {server_url}/admin/api/v1/eventstreams/{collection_name}/shape            | GET    | Get the SHACL shape the eventstream                                         |
| **Put shape**           | {server_url}/admin/api/v1/eventstreams/{collection_name}/shape            | PUT    | Add a SHACL shape to an eventstream or change it if one is already present  |
| **Get views**           | {server_url}/admin/api/v1/eventstreams/{collection_name}/views            | GET    | Get all the views of the eventstream                                        |
| **Put view**            | {server_url}/admin/api/v1/eventstreams/{collection_name}/views            | PUT    | Add a new view to the eventstream or update an existing one                 |
| **Get view**            | {server_url}/admin/api/v1/eventstreams/{collection_name}/views/{viewName} | GET    | Get the view with the given name of the chosen eventstream                  |
| **Delete view**         | {server_url}/admin/api/v1/eventstreams/{collection_name}/views/{viewName} | DELETE | Remove a view with the given name from the chosen eventstream               |


## Input data

The body of any ``put`` request must contain a rdf model in the turtle format. Otherwise a BAD REQUEST status is returned.
Every resource describing a stream, shape or view must be a uri with the prefix ``https://w3id.org/ldes#``.
In the future, this will not be a requirement but instead a preconfigured prefix will be amended to any local resource.


## LDES config SHACL shape validation

When a ``PUT`` request is sent to update the config of the event streams, the SHACL shape of this config in the request
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


[] a ldes:EventStream ;
    ldes:timestampPath dcterms:created ;
    ldes:versionOfPath dcterms:isVersionOf ;
    tree:shape [
        sh:closed true;
        a sh:NodeShape ;
    ] .
```

### LDES SHACL shape

In the shape of the SHACL shape are none of the properties required to be provided, but no extra properties can be
provided then those that are included in the following example.

```
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

ldes:view1 a ldes:View ;
  tree:viewDescription [
    example:fragmentationStrategy [
        a example:Fragmentation ;
        example:property ldes:propertyPath
    ]
] .
```