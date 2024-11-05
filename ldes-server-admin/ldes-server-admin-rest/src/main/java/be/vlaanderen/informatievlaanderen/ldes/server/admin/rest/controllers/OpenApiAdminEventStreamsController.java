package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.jena.rdf.model.Model;

import java.util.List;

import static org.apache.jena.riot.WebContent.*;

@Tag(name = "Event Streams")
@SuppressWarnings("java:S2479") // whitespace needed for examples
public interface OpenApiAdminEventStreamsController {
	@Operation(summary = "Retrieve list of configured Event Streams")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					@prefix ldes: <https://w3id.org/ldes#> .
					@prefix dcterms: <http://purl.org/dc/terms/> .
					@prefix tree: <https://w3id.org/tree#>.
					@prefix sh:   <http://www.w3.org/ns/shacl#> .
					@prefix server: <http://localhost:8080/> .
					@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
					@prefix event-stream-1: <http://localhost:8080/event-stream-1/> .
					@prefix event-stream-2: <http://localhost:8080/event-stream-2/> .
					
					server:event-stream-1 a ldes:EventStream ;
						ldes:timestampPath dcterms:created ;
						ldes:versionOfPath dcterms:isVersionOf ;
						ldes:createVersions false ;
						tree:shape event-stream-1:shape .
					
					server:event-stream-2 a ldes:EventStream ;
						ldes:timestampPath dcterms:created ;
						ldes:versionOfPath dcterms:isVersionOf ;
						ldes:createVersions true ;
						tree:shape event-stream-2:shape .
						ldes:view event-stream-2:by-page .
					
					event-stream-2:by-page tree:viewDescription [
						tree:pageSize "100"^^<http://www.w3.org/2001/XMLSchema#int> ;
						tree:fragmentationStrategy () ;
					] .
					
					event-stream-1:shape a sh:NodeShape .
					
					event-stream-2:shape a sh:NodeShape .
					""")),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					<http://localhost:8080/event-stream-1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
					   <http://localhost:8080/event-stream-1> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
					   <http://localhost:8080/event-stream-1> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
					   <http://localhost:8080/event-stream-1> <https://w3id.org/ldes#createVersions> "false"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					   <http://localhost:8080/event-stream-1> <https://w3id.org/tree#shape> <http://localhost:8080/event-stream-1/shape> .
					   <http://localhost:8080/event-stream-2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
					   <http://localhost:8080/event-stream-2> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
					   <http://localhost:8080/event-stream-2> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
					   <http://localhost:8080/event-stream-2> <https://w3id.org/ldes#createVersions> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					   <http://localhost:8080/event-stream-2> <https://w3id.org/tree#shape> <http://localhost:8080/event-stream-2/shape> .
					   <https://w3id.org/ldes#view> <http://localhost:8080/event-stream-2/by-page> ""^^<http://www.w3.org/2001/XMLSchema#integer> .
					   _:genid1 <https://w3id.org/tree#pageSize> "100"^^<http://www.w3.org/2001/XMLSchema#int> .
					   _:genid1 <https://w3id.org/tree#fragmentationStrategy> <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> .
					   <http://localhost:8080/event-stream-2/by-page> <https://w3id.org/tree#viewDescription> _:genid1 .
					   <http://localhost:8080/event-stream-1/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
					   <http://localhost:8080/event-stream-2/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
					""")),
			@Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
					[{"@id":"_:b0","https://w3id.org/tree#pageSize":[{"@value":"100","@type":"http://www.w3.org/2001/XMLSchema#int"}],"https://w3id.org/tree#fragmentationStrategy":[{"@id":"http://www.w3.org/1999/02/22-rdf-syntax-ns#nil"}]},{"@id":"http://localhost:8080/event-stream-1","@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://purl.org/dc/terms/created"}],"https://w3id.org/ldes#versionOfPath":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"https://w3id.org/ldes#createVersions":[{"@value":false}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/event-stream-1/shape"}]},{"@id":"http://localhost:8080/event-stream-1/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"]},{"@id":"http://localhost:8080/event-stream-2","@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://purl.org/dc/terms/created"}],"https://w3id.org/ldes#versionOfPath":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"https://w3id.org/ldes#createVersions":[{"@value":true}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/event-stream-2/shape"}]},{"@id":"http://localhost:8080/event-stream-2/by-page","https://w3id.org/tree#viewDescription":[{"@id":"_:b0"}]},{"@id":"http://localhost:8080/event-stream-2/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"]},{"@id":"http://purl.org/dc/terms/created"},{"@id":"http://purl.org/dc/terms/isVersionOf"},{"@id":"http://www.w3.org/1999/02/22-rdf-syntax-ns#nil"},{"@id":"http://www.w3.org/ns/shacl#NodeShape"},{"@id":"https://w3id.org/ldes#EventStream"},{"@id":"https://w3id.org/ldes#view","http://localhost:8080/event-stream-2/by-page":[{"@value":"","@type":"http://www.w3.org/2001/XMLSchema#integer"}]}]
					"""))
	})
	List<EventStreamTO> getEventStreams();

	@Operation(summary = "Create an Event Stream based on the provided config")
	@ApiResponse(responseCode = "201", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = {
					@ExampleObject(name = "Version Objects ingestion", description = "An event stream that ingests version objects", value = """
							@prefix ldes: <https://w3id.org/ldes#> .
							@prefix dcterms: <http://purl.org/dc/terms/> .
							@prefix prov: <http://www.w3.org/ns/prov#> .
							@prefix tree: <https://w3id.org/tree#>.
							@prefix sh:   <http://www.w3.org/ns/shacl#> .
							@prefix server: <http://localhost:8080/> .
							@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
							@prefix event-stream: <http://localhost:8080/event-stream/> .
							
							server:event-stream a ldes:EventStream ;
								ldes:timestampPath dcterms:created ;
								ldes:versionOfPath dcterms:isVersionOf ;
								tree:shape event-stream:shape .
							
							event-stream:shape a sh:NodeShape .
							"""),
					@ExampleObject(name = "State Objects ingestion", description = "An event stream that ingests state objects", value = """
							@prefix ldes: <https://w3id.org/ldes#> .
							@prefix dcterms: <http://purl.org/dc/terms/> .
							@prefix prov: <http://www.w3.org/ns/prov#> .
							@prefix tree: <https://w3id.org/tree#>.
							@prefix sh:   <http://www.w3.org/ns/shacl#> .
							@prefix server: <http://localhost:8080/> .
							@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
							@prefix event-stream: <http://localhost:8080/event-stream/> .
							
							server:event-stream a ldes:EventStream ;
								ldes:timestampPath dcterms:created ;
								ldes:versionOfPath dcterms:isVersionOf ;
								ldes:createVersions true ;
								tree:shape event-stream:shape .
							
							event-stream:shape a sh:NodeShape .
							"""),
					@ExampleObject(name = "State Objects ingestion with custom delimiter", description = "An event stream that ingests state objects with a custom version delimiter", value = """
							@prefix ldes: <https://w3id.org/ldes#> .
							@prefix dcterms: <http://purl.org/dc/terms/> .
							@prefix prov: <http://www.w3.org/ns/prov#> .
							@prefix tree: <https://w3id.org/tree#>.
							@prefix sh:   <http://www.w3.org/ns/shacl#> .
							@prefix server: <http://localhost:8080/> .
							@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
							@prefix event-stream: <http://localhost:8080/event-stream/> .
							
							server:event-stream a ldes:EventStream ;
								ldes:timestampPath dcterms:created ;
								ldes:versionOfPath dcterms:isVersionOf ;
								ldes:createVersions true ;
								ldes:versionDelimiter "&version=" ;
								tree:shape event-stream:shape .
							
							event-stream:shape a sh:NodeShape .
							""")
			}),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = {
					@ExampleObject(name = "Version Objects ingestion", description = "An event stream that ingests version objects", value = """
							<http://localhost:8080/event-stream> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
							<http://localhost:8080/event-stream> <https://w3id.org/tree#shape> <http://localhost:8080/event-stream/shape> .
							<http://localhost:8080/event-stream/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
							"""),
					@ExampleObject(name = "State Objects ingestion", description = "An event stream that ingests state objects", value = """
							<http://localhost:8080/event-stream> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#createVersions> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
							<http://localhost:8080/event-stream> <https://w3id.org/tree#shape> <http://localhost:8080/event-stream/shape> .
							<http://localhost:8080/event-stream/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
							"""),
					@ExampleObject(name = "State Objects ingestion with custom delimiter", description = "An event stream that ingests state objects with a custom version delimiter", value = """
							<http://localhost:8080/event-stream> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#createVersions> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#versionDelimiter> "&version="^^<http://www.w3.org/2001/XMLSchema#string> .
							<http://localhost:8080/event-stream> <https://w3id.org/tree#shape> <http://localhost:8080/event-stream/shape> .
							<http://localhost:8080/event-stream/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
							""")
			}),
			@Content(mediaType = contentTypeJSONLD, examples = {
					@ExampleObject(name = "Version Objects ingestion", description = "An event stream that ingests version objects", value = """
							{
								"@context": {
									"dcterms": "http://purl.org/dc/terms/",
									"event-stream": "http://localhost:8080/event-stream/",
									"ldes": "https://w3id.org/ldes#",
									"tree": "https://w3id.org/tree#"
								},
								"@id": "http://localhost:8080/event-stream",
								"@type": "ldes:EventStream",
								"ldes:timestampPath": {
									"@id": "dcterms:created"
								},
								"ldes:versionOfPath": {
									"@id": "dcterms:isVersionOf"
								},
								"tree:shape": {
									"@id": "event-stream:shape",
									"@type": "http://www.w3.org/ns/shacl#NodeShape"
								}
							}
							"""),
					@ExampleObject(name = "State Objects ingestion", description = "An event stream that ingests state objects", value = """
							{
								"@context": {
									"dcterms": "http://purl.org/dc/terms/",
									"event-stream": "http://localhost:8080/event-stream/",
									"ldes": "https://w3id.org/ldes#",
									"tree": "https://w3id.org/tree#"
								},
								"@id": "http://localhost:8080/event-stream",
								"@type": "ldes:EventStream",
								"ldes:timestampPath": {
									"@id": "dcterms:created"
								},
								"ldes:versionOfPath": {
									"@id": "dcterms:isVersionOf"
								},
								"ldes:createVersions": true,
								"tree:shape": {
									"@id": "event-stream:shape",
									"@type": "http://www.w3.org/ns/shacl#NodeShape"
								}
							}
							"""),
					@ExampleObject(name = "State Objects ingestion with custom delimiter", description = "An event stream that ingests state objects with a custom version delimiter", value = """
							{
								"@context": {
									"dcterms": "http://purl.org/dc/terms/",
									"event-stream": "http://localhost:8080/event-stream/",
									"ldes": "https://w3id.org/ldes#",
									"tree": "https://w3id.org/tree#"
								},
								"@id": "http://localhost:8080/event-stream",
								"@type": "ldes:EventStream",
								"ldes:timestampPath": {
									"@id": "dcterms:created"
								},
								"ldes:versionOfPath": {
									"@id": "dcterms:isVersionOf"
								},
								"ldes:createVersions": true,
								"ldes:versionDelimiter": "&version=",
								"tree:shape": {
									"@id": "event-stream:shape",
									"@type": "http://www.w3.org/ns/shacl#NodeShape"
								}
							}
							""")
			})
	})
	@ApiResponse(responseCode = "400", description = "The provided config is not valid", content = @Content)
	EventStreamTO createEventStream(@RequestBody(content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = {
					@ExampleObject(name = "Version Objects ingestion", description = "An event stream that ingests version objects", value = """
							@prefix ldes:           <https://w3id.org/ldes#> .
							@prefix dcterms:        <http://purl.org/dc/terms/> .
							@prefix prov:           <http://www.w3.org/ns/prov#> .
							@prefix tree:           <https://w3id.org/tree#>.
							@prefix sh:             <http://www.w3.org/ns/shacl#> .
							@prefix server:         <http://localhost:8080/> .
							@prefix xsd:            <http://www.w3.org/2001/XMLSchema#> .
							@prefix event-stream:   <http://localhost:8080/event-stream/> .
							
							server:event-stream a ldes:EventStream ;
								ldes:timestampPath dcterms:created ;
								ldes:versionOfPath dcterms:isVersionOf ;
								tree:shape event-stream:shape .
							
							event-stream:shape a sh:NodeShape .
							"""),
					@ExampleObject(name = "State Objects ingestion", description = "An event stream that ingests state objects", value = """
							@prefix ldes:           <https://w3id.org/ldes#> .
							@prefix dcterms:        <http://purl.org/dc/terms/> .
							@prefix prov:           <http://www.w3.org/ns/prov#> .
							@prefix tree:           <https://w3id.org/tree#>.
							@prefix sh:             <http://www.w3.org/ns/shacl#> .
							@prefix server:         <http://localhost:8080/> .
							@prefix xsd:            <http://www.w3.org/2001/XMLSchema#> .
							@prefix event-stream:   <http://localhost:8080/event-stream/> .
							
							server:event-stream a ldes:EventStream ;
								ldes:timestampPath dcterms:created ;
								ldes:versionOfPath dcterms:isVersionOf ;
								ldes:createVersions true ;
								tree:shape event-stream:shape .
							
							event-stream:shape a sh:NodeShape .
							"""),
					@ExampleObject(name = "State Objects ingestion with custom delimiter", description = "An event stream that ingests state objects with a custom version delimiter", value = """
							@prefix ldes:           <https://w3id.org/ldes#> .
							@prefix dcterms:        <http://purl.org/dc/terms/> .
							@prefix prov:           <http://www.w3.org/ns/prov#> .
							@prefix tree:           <https://w3id.org/tree#>.
							@prefix sh:             <http://www.w3.org/ns/shacl#> .
							@prefix server:         <http://localhost:8080/> .
							@prefix xsd:            <http://www.w3.org/2001/XMLSchema#> .
							@prefix event-stream:   <http://localhost:8080/event-stream/> .
							
							server:event-stream a ldes:EventStream ;
								ldes:timestampPath dcterms:created ;
								ldes:versionOfPath dcterms:isVersionOf ;
								ldes:createVersions true ;
								ldes:versionDelimiter "&version=" ;
								tree:shape event-stream:shape .
							
							event-stream:shape a sh:NodeShape .
							""")
			}),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = {
					@ExampleObject(name = "Version Objects ingestion", description = "An event stream that ingests version objects", value = """
							<http://localhost:8080/event-stream> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
							<http://localhost:8080/event-stream> <https://w3id.org/tree#shape> <http://localhost:8080/event-stream/shape> .
							<http://localhost:8080/event-stream/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
							"""),
					@ExampleObject(name = "State Objects ingestion", description = "An event stream that ingests state objects", value = """
							<http://localhost:8080/event-stream> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#createVersions> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
							<http://localhost:8080/event-stream> <https://w3id.org/tree#shape> <http://localhost:8080/event-stream/shape> .
							<http://localhost:8080/event-stream/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
							"""),
					@ExampleObject(name = "State Objects ingestion with custom delimiter", description = "An event stream that ingests state objects with a custom version delimiter", value = """
							<http://localhost:8080/event-stream> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#createVersions> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
							<http://localhost:8080/event-stream> <https://w3id.org/ldes#versionDelimiter> "&version="^^<http://www.w3.org/2001/XMLSchema#string> .
							<http://localhost:8080/event-stream> <https://w3id.org/tree#shape> <http://localhost:8080/event-stream/shape> .
							<http://localhost:8080/event-stream/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
							""")
			}),
			@Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = {
					@ExampleObject(name = "Version Objects ingestion", description = "An event stream that ingests version objects", value = """
							{
								"@context": {
									"dcterms": "http://purl.org/dc/terms/",
									"event-stream": "http://localhost:8080/event-stream/",
									"ldes": "https://w3id.org/ldes#",
									"tree": "https://w3id.org/tree#"
								},
								"@id": "http://localhost:8080/event-stream",
								"@type": "ldes:EventStream",
								"ldes:timestampPath": {
									"@id": "dcterms:created"
								},
								"ldes:versionOfPath": {
									"@id": "dcterms:isVersionOf"
								},
								"tree:shape": {
									"@id": "event-stream:shape",
									"@type": "http://www.w3.org/ns/shacl#NodeShape"
								}
							}
							"""),
					@ExampleObject(name = "State Objects ingestion", description = "An event stream that ingests state objects", value = """
							{
								"@context": {
									"dcterms": "http://purl.org/dc/terms/",
									"event-stream": "http://localhost:8080/event-stream/",
									"ldes": "https://w3id.org/ldes#",
									"tree": "https://w3id.org/tree#"
								},
								"@id": "http://localhost:8080/event-stream",
								"@type": "ldes:EventStream",
								"ldes:timestampPath": {
									"@id": "dcterms:created"
								},
								"ldes:versionOfPath": {
									"@id": "dcterms:isVersionOf"
								},
								"ldes:createVersions": true,
								"tree:shape": {
									"@id": "event-stream:shape",
									"@type": "http://www.w3.org/ns/shacl#NodeShape"
								}
							}
							"""),
					@ExampleObject(name = "State Objects ingestion with custom delimiter", description = "An event stream that ingests state objects with a custom version delimiter", value = """
							{
								"@context": {
									"dcterms": "http://purl.org/dc/terms/",
									"event-stream": "http://localhost:8080/event-stream/",
									"ldes": "https://w3id.org/ldes#",
									"tree": "https://w3id.org/tree#"
								},
								"@id": "http://localhost:8080/event-stream",
								"@type": "ldes:EventStream",
								"ldes:timestampPath": {
									"@id": "dcterms:created"
								},
								"ldes:versionOfPath": {
									"@id": "dcterms:isVersionOf"
								},
								"ldes:createVersions": true,
								"ldes:versionDelimiter": "&version=",
								"tree:shape": {
									"@id": "event-stream:shape",
									"@type": "http://www.w3.org/ns/shacl#NodeShape"
								}
							}
							""")
			})
	}) Model eventStreamModel);

	@Operation(summary = "Retrieve specific Event Stream configuration")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					@prefix ldes: <https://w3id.org/ldes#> .
					@prefix dcterms: <http://purl.org/dc/terms/> .
					@prefix tree: <https://w3id.org/tree#>.
					@prefix sh:   <http://www.w3.org/ns/shacl#> .
					@prefix server: <http://localhost:8080/> .
					@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
					@prefix event-stream: <http://localhost:8080/event-stream/> .
					
					server:event-stream a ldes:EventStream ;
						ldes:timestampPath dcterms:created ;
						ldes:versionOfPath dcterms:isVersionOf ;
						ldes:createVersions false ;
						tree:shape event-stream:shape .
						ldes:view event-stream:pagination .
					
					event-stream:pagination tree:viewDescription [
						tree:pageSize "100"^^<http://www.w3.org/2001/XMLSchema#int> ;
						tree:fragmentationStrategy () ;
					] .
					
					event-stream:shape a sh:NodeShape .
					""")),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					<http://localhost:8080/event-stream> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
					               <http://localhost:8080/event-stream> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
					               <http://localhost:8080/event-stream> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
					               <http://localhost:8080/event-stream> <https://w3id.org/ldes#createVersions> "false"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					               <http://localhost:8080/event-stream> <https://w3id.org/tree#shape> <http://localhost:8080/event-stream/shape> .
					               <https://w3id.org/ldes#view> <http://localhost:8080/event-stream/pagination> ""^^<http://www.w3.org/2001/XMLSchema#integer> .
					               _:genid1 <https://w3id.org/tree#pageSize> "100"^^<http://www.w3.org/2001/XMLSchema#int> .
					               _:genid1 <https://w3id.org/tree#fragmentationStrategy> <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> .
					               <http://localhost:8080/event-stream/pagination> <https://w3id.org/tree#viewDescription> _:genid1 .
					               <http://localhost:8080/event-stream/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
					""")),
			@Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
					[{"@id":"_:b0","https://w3id.org/tree#pageSize":[{"@value":"100","@type":"http://www.w3.org/2001/XMLSchema#int"}],"https://w3id.org/tree#fragmentationStrategy":[{"@id":"http://www.w3.org/1999/02/22-rdf-syntax-ns#nil"}]},{"@id":"http://localhost:8080/event-stream","@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://purl.org/dc/terms/created"}],"https://w3id.org/ldes#versionOfPath":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"https://w3id.org/ldes#createVersions":[{"@value":false}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/event-stream/shape"}]},{"@id":"http://localhost:8080/event-stream/pagination","https://w3id.org/tree#viewDescription":[{"@id":"_:b0"}]},{"@id":"http://localhost:8080/event-stream/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"]},{"@id":"http://purl.org/dc/terms/created"},{"@id":"http://purl.org/dc/terms/isVersionOf"},{"@id":"http://www.w3.org/1999/02/22-rdf-syntax-ns#nil"},{"@id":"http://www.w3.org/ns/shacl#NodeShape"},{"@id":"https://w3id.org/ldes#EventStream"},{"@id":"https://w3id.org/ldes#view","http://localhost:8080/event-stream/pagination":[{"@value":"","@type":"http://www.w3.org/2001/XMLSchema#integer"}]}]
					"""))
	})
	@ApiResponse(responseCode = "404", description = "Event Stream with provided collection name could not be found", content = @Content)
	EventStreamTO getEventStream(@Parameter(example = "event-stream") String collectionName);

	@Operation(summary = "Delete an Event Stream")
	@ApiResponse(responseCode = "200", description = "Event Stream has been successfully deleted")
	@ApiResponse(responseCode = "404", description = "Event Stream with provided collection name could not be found")
	void deleteEventStream(@Parameter(example = "event-stream") String collectionName);

	@Operation(summary = "Close an Event Stream")
	@ApiResponse(responseCode = "200", description = "Event Stream is successfully closed and all related fragments are made immutable")
	@ApiResponse(responseCode = "404", description = "Event Stream with provided collection name could not be found")
	void closeEventStream(@Parameter(example = "event-stream") String collectionName);

	@Operation(summary = "Update the Event Source of an Event Stream")
	@ApiResponse(responseCode = "200", description = "Event Source has been successfully updated", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					@prefix ldes: <https://w3id.org/ldes#> .
					@prefix dcterms: <http://purl.org/dc/terms/> .
					@prefix tree: <https://w3id.org/tree#>.
					
					<> a ldes:EventSource ;
					    ldes:retentionPolicy [
					        a ldes:DurationAgoPolicy ;
					        tree:value "PT5S"^^<http://www.w3.org/2001/XMLSchema#duration> ;
					      ] .
					""")),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					<http://njh.me/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventSource> .
					<http://njh.me/> <https://w3id.org/ldes#retentionPolicy> _:genid1 .
					_:genid1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#DurationAgoPolicy> .
					_:genid1 <https://w3id.org/tree#value> "PT5S"^^<http://www.w3.org/2001/XMLSchema#duration> .
					""")),
			@Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
					[{"@id":"_:b0","@type":["https://w3id.org/ldes#DurationAgoPolicy"],"https://w3id.org/tree#value":[{"@value":"PT5S","@type":"http://www.w3.org/2001/XMLSchema#duration"}]},{"@id":"http://njh.me/","@type":["https://w3id.org/ldes#EventSource"],"https://w3id.org/ldes#retentionPolicy":[{"@id":"_:b0"}]},{"@id":"https://w3id.org/ldes#DurationAgoPolicy"},{"@id":"https://w3id.org/ldes#EventSource"}]
					"""))})
	@ApiResponse(responseCode = "404", description = "Event Stream with provided collection name could not be found")
	void updateEventSource(@Parameter(example = "event-stream") String collectionName,
	                       @RequestBody(content = {
			                       @Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
											@prefix ldes: <https://w3id.org/ldes#> .
											@prefix dcterms: <http://purl.org/dc/terms/> .
											@prefix tree: <https://w3id.org/tree#>.

											<> a ldes:EventSource ;
												ldes:retentionPolicy [
													a ldes:DurationAgoPolicy ;
													tree:value "PT5S"^^<http://www.w3.org/2001/XMLSchema#duration> ;
												] .
											""")),
			                       @Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
											<http://njh.me/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventSource> .
											<http://njh.me/> <https://w3id.org/ldes#retentionPolicy> _:genid1 .
											_:genid1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#DurationAgoPolicy> .
											_:genid1 <https://w3id.org/tree#value> "PT5S"^^<http://www.w3.org/2001/XMLSchema#duration> .
											""")),
			                       @Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
											[{"@id":"_:b0","@type":["https://w3id.org/ldes#DurationAgoPolicy"],"https://w3id.org/tree#value":[{"@value":"PT5S","@type":"http://www.w3.org/2001/XMLSchema#duration"}]},{"@id":"http://njh.me/","@type":["https://w3id.org/ldes#EventSource"],"https://w3id.org/ldes#retentionPolicy":[{"@id":"_:b0"}]},{"@id":"https://w3id.org/ldes#DurationAgoPolicy"},{"@id":"https://w3id.org/ldes#EventSource"}]
											"""))
	                       }) Model eventSourceModel);
}
