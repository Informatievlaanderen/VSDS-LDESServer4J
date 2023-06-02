package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
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
public interface OpenApiEventStreamsController {
	@Operation(summary = "Retrieve list of configured Event Streams")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					@prefix ldes: <https://w3id.org/ldes#> .
					@prefix custom: <http://example.org/> .
					@prefix dcterms: <http://purl.org/dc/terms/> .
					@prefix tree: <https://w3id.org/tree#>.
					@prefix sh:   <http://www.w3.org/ns/shacl#> .
					@prefix server: <http://localhost:8080/> .
					@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
					@prefix example: <http://example.org/> .
					@prefix parcels: <http://localhost:8080/parcels/> .
					@prefix mobility-hindrances: <http://localhost:8080/mobility-hindrances/> .

					server:mobility-hindrances a ldes:EventStream ;
						ldes:timestampPath dcterms:created ;
						ldes:versionOfPath dcterms:isVersionOf ;
						custom:memberType <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> ;
						custom:hasDefaultView "false"^^xsd:boolean ;
						tree:shape mobility-hindrances:shape .

					server:parcels a ldes:EventStream ;
						ldes:timestampPath dcterms:created ;
						ldes:versionOfPath dcterms:isVersionOf ;
						custom:memberType <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> ;
						custom:hasDefaultView "true"^^xsd:boolean ;
						tree:shape server:shape .
						ldes:view server:pagination .

					parcels:pagination tree:viewDescription [
						example:fragmentationStrategy [
							a example:Fragmentation ;
							example:name "pagination" ;
							example:memberLimit "100"
						] ;
					] .

					parcels:shape a sh:NodeShape ;
						sh:nodeShape [
							sh:closed true ;
							sh:propertyShape []
							] ;
						sh:deactivated true .

					mobility-hindrances:shape a sh:NodeShape ;
					sh:nodeShape [
						sh:closed true ;
						sh:propertyShape []
						] ;
					sh:deactivated true .
					""")),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					<http://localhost:8080/mobility-hindrances> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
					<http://localhost:8080/mobility-hindrances> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
					<http://localhost:8080/mobility-hindrances> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
					<http://localhost:8080/mobility-hindrances> <http://example.org/memberType> <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> .
					<http://localhost:8080/mobility-hindrances> <http://example.org/hasDefaultView> "false"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					<http://localhost:8080/mobility-hindrances> <https://w3id.org/tree#shape> <http://localhost:8080/mobility-hindrances/shape> .
					<http://localhost:8080/parcels> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
					<http://localhost:8080/parcels> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
					<http://localhost:8080/parcels> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
					<http://localhost:8080/parcels> <http://example.org/memberType> <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> .
					<http://localhost:8080/parcels> <http://example.org/hasDefaultView> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					<http://localhost:8080/parcels> <https://w3id.org/tree#shape> <http://localhost:8080/shape> .
					<https://w3id.org/ldes#view> <http://localhost:8080/pagination> ""^^<http://www.w3.org/2001/XMLSchema#integer> .
					_:genid2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Fragmentation> .
					_:genid2 <http://example.org/name> "pagination" .
					_:genid2 <http://example.org/memberLimit> "100" .
					_:genid1 <http://example.org/fragmentationStrategy> _:genid2 .
					<http://localhost:8080/parcels/pagination> <https://w3id.org/tree#viewDescription> _:genid1 .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#nodeShape> _:genid3 .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#deactivated> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					_:genid3 <http://www.w3.org/ns/shacl#closed> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					_:genid3 <http://www.w3.org/ns/shacl#propertyShape> _:genid4 .
					<http://localhost:8080/mobility-hindrances/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
					<http://localhost:8080/mobility-hindrances/shape> <http://www.w3.org/ns/shacl#nodeShape> _:genid5 .
					<http://localhost:8080/mobility-hindrances/shape> <http://www.w3.org/ns/shacl#deactivated> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					_:genid5 <http://www.w3.org/ns/shacl#closed> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					_:genid5 <http://www.w3.org/ns/shacl#propertyShape> _:genid6 .
					""")),
			@Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
					[{"@id":"_:b0","@type":["http://example.org/Fragmentation"],"http://example.org/name":[{"@value":"pagination"}],"http://example.org/memberLimit":[{"@value":"100"}]},{"@id":"_:b1","http://example.org/fragmentationStrategy":[{"@id":"_:b0"}]},{"@id":"_:b2","http://www.w3.org/ns/shacl#closed":[{"@value":true}],"http://www.w3.org/ns/shacl#propertyShape":[{"@id":"_:b3"}]},{"@id":"_:b3"},{"@id":"_:b4","http://www.w3.org/ns/shacl#closed":[{"@value":true}],"http://www.w3.org/ns/shacl#propertyShape":[{"@id":"_:b5"}]},{"@id":"_:b5"},{"@id":"http://example.org/Fragmentation"},{"@id":"http://localhost:8080/mobility-hindrances","@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://purl.org/dc/terms/created"}],"https://w3id.org/ldes#versionOfPath":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"http://example.org/memberType":[{"@id":"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder"}],"http://example.org/hasDefaultView":[{"@value":false}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/mobility-hindrances/shape"}]},{"@id":"http://localhost:8080/mobility-hindrances/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"],"http://www.w3.org/ns/shacl#nodeShape":[{"@id":"_:b4"}],"http://www.w3.org/ns/shacl#deactivated":[{"@value":true}]},{"@id":"http://localhost:8080/parcels","@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://purl.org/dc/terms/created"}],"https://w3id.org/ldes#versionOfPath":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"http://example.org/memberType":[{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"}],"http://example.org/hasDefaultView":[{"@value":true}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/shape"}]},{"@id":"http://localhost:8080/parcels/pagination","https://w3id.org/tree#viewDescription":[{"@id":"_:b1"}]},{"@id":"http://localhost:8080/parcels/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"],"http://www.w3.org/ns/shacl#nodeShape":[{"@id":"_:b2"}],"http://www.w3.org/ns/shacl#deactivated":[{"@value":true}]},{"@id":"http://localhost:8080/shape"},{"@id":"http://purl.org/dc/terms/created"},{"@id":"http://purl.org/dc/terms/isVersionOf"},{"@id":"http://www.w3.org/ns/shacl#NodeShape"},{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"},{"@id":"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder"},{"@id":"https://w3id.org/ldes#EventStream"},{"@id":"https://w3id.org/ldes#view","http://localhost:8080/pagination":[{"@value":"","@type":"http://www.w3.org/2001/XMLSchema#integer"}]}]
					"""))
	})
	List<EventStreamResponse> getEventStreams();

	@Operation(summary = "Create or update an Event Stream based on the provided config")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					@prefix ldes: <https://w3id.org/ldes#> .
					@prefix custom: <http://example.org/> .
					@prefix dcterms: <http://purl.org/dc/terms/> .
					@prefix tree: <https://w3id.org/tree#>.
					@prefix sh:   <http://www.w3.org/ns/shacl#> .
					@prefix server: <http://localhost:8080/> .
					@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
					@prefix example: <http://example.org/> .
					@prefix parcels: <http://localhost:8080/parcels/> .

					server:parcels a ldes:EventStream ;
						ldes:timestampPath dcterms:created ;
						ldes:versionOfPath dcterms:isVersionOf ;
						custom:memberType <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> ;
						custom:hasDefaultView "true"^^xsd:boolean ;
						tree:shape parcels:shape .

					parcels:shape a sh:NodeShape ;
						sh:nodeShape [
							sh:closed true ;
							sh:propertyShape []
							] ;
						sh:deactivated true .
					""")),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					<http://localhost:8080/parcels> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
					<http://localhost:8080/parcels> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
					<http://localhost:8080/parcels> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
					<http://localhost:8080/parcels> <http://example.org/memberType> <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> .
					<http://localhost:8080/parcels> <http://example.org/hasDefaultView> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					<http://localhost:8080/parcels> <https://w3id.org/tree#shape> <http://localhost:8080/parcels/shape> .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#nodeShape> _:genid1 .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#deactivated> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					_:genid1 <http://www.w3.org/ns/shacl#closed> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					_:genid1 <http://www.w3.org/ns/shacl#propertyShape> _:genid2 .
					""")),
			@Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
					[{"@id":"_:b0","http://www.w3.org/ns/shacl#closed":[{"@value":true}],"http://www.w3.org/ns/shacl#propertyShape":[{"@id":"_:b1"}]},{"@id":"_:b1"},{"@id":"http://localhost:8080/parcels","@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://purl.org/dc/terms/created"}],"https://w3id.org/ldes#versionOfPath":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"http://example.org/memberType":[{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"}],"http://example.org/hasDefaultView":[{"@value":true}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/parcels/shape"}]},{"@id":"http://localhost:8080/parcels/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"],"http://www.w3.org/ns/shacl#nodeShape":[{"@id":"_:b0"}],"http://www.w3.org/ns/shacl#deactivated":[{"@value":true}]},{"@id":"http://purl.org/dc/terms/created"},{"@id":"http://purl.org/dc/terms/isVersionOf"},{"@id":"http://www.w3.org/ns/shacl#NodeShape"},{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"},{"@id":"https://w3id.org/ldes#EventStream"}]
												"""))
	})
	@ApiResponse(responseCode = "400", description = "The provided config is not valid")
	EventStreamResponse putEventStream(
			@RequestBody(content = {
					@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
							@prefix ldes: <https://w3id.org/ldes#> .
							@prefix custom: <http://example.org/> .
							@prefix dcterms: <http://purl.org/dc/terms/> .
							@prefix tree: <https://w3id.org/tree#>.
							@prefix sh:   <http://www.w3.org/ns/shacl#> .
							@prefix server: <http://localhost:8080/> .
							@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
							@prefix example: <http://example.org/> .
							@prefix parcels: <http://localhost:8080/parcels/> .

							server:parcels a ldes:EventStream ;
								ldes:timestampPath dcterms:created ;
								ldes:versionOfPath dcterms:isVersionOf ;
								custom:memberType <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> ;
								custom:hasDefaultView "true"^^xsd:boolean ;
								tree:shape parcels:shape .

							parcels:shape a sh:NodeShape ;
								sh:nodeShape [
									sh:closed true ;
									sh:propertyShape []
									] ;
								sh:deactivated true .
							""")),
					@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
							<http://localhost:8080/parcels> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
							<http://localhost:8080/parcels> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
							<http://localhost:8080/parcels> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
							<http://localhost:8080/parcels> <http://example.org/memberType> <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> .
							<http://localhost:8080/parcels> <http://example.org/hasDefaultView> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
							<http://localhost:8080/parcels> <https://w3id.org/tree#shape> <http://localhost:8080/parcels/shape> .
							<http://localhost:8080/parcels/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
							<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#nodeShape> _:genid1 .
							<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#deactivated> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
							_:genid1 <http://www.w3.org/ns/shacl#closed> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
							_:genid1 <http://www.w3.org/ns/shacl#propertyShape> _:genid2 .
							""")),
					@Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
							[{"@id":"_:b0","http://www.w3.org/ns/shacl#closed":[{"@value":true}],"http://www.w3.org/ns/shacl#propertyShape":[{"@id":"_:b1"}]},{"@id":"_:b1"},{"@id":"http://localhost:8080/parcels","@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://purl.org/dc/terms/created"}],"https://w3id.org/ldes#versionOfPath":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"http://example.org/memberType":[{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"}],"http://example.org/hasDefaultView":[{"@value":true}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/parcels/shape"}]},{"@id":"http://localhost:8080/parcels/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"],"http://www.w3.org/ns/shacl#nodeShape":[{"@id":"_:b0"}],"http://www.w3.org/ns/shacl#deactivated":[{"@value":true}]},{"@id":"http://purl.org/dc/terms/created"},{"@id":"http://purl.org/dc/terms/isVersionOf"},{"@id":"http://www.w3.org/ns/shacl#NodeShape"},{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"},{"@id":"https://w3id.org/ldes#EventStream"}]
														"""))
			}) Model eventStreamModel);

	@Operation(summary = "Retrieve specific Event Stream configuration")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					@prefix ldes: <https://w3id.org/ldes#> .
					@prefix custom: <http://example.org/> .
					@prefix dcterms: <http://purl.org/dc/terms/> .
					@prefix tree: <https://w3id.org/tree#>.
					@prefix sh:   <http://www.w3.org/ns/shacl#> .
					@prefix server: <http://localhost:8080/> .
					@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
					@prefix example: <http://example.org/> .
					@prefix parcels: <http://localhost:8080/parcels/> .

					server:parcels a ldes:EventStream ;
						ldes:timestampPath dcterms:created ;
						ldes:versionOfPath dcterms:isVersionOf ;
						custom:memberType <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> ;
						custom:hasDefaultView "true"^^xsd:boolean ;
						tree:shape parcels:shape .
						ldes:view parcels:pagination .

					parcels:pagination tree:viewDescription [
						example:fragmentationStrategy [
							a example:Fragmentation ;
							example:name "pagination" ;
							example:memberLimit "100"
						] ;
					] .

					parcels:shape a sh:NodeShape ;
						sh:nodeShape [
							sh:closed true ;
							sh:propertyShape []
							] ;
						sh:deactivated true .
					""")),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					<http://localhost:8080/parcels> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
					<http://localhost:8080/parcels> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
					<http://localhost:8080/parcels> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
					<http://localhost:8080/parcels> <http://example.org/memberType> <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> .
					<http://localhost:8080/parcels> <http://example.org/hasDefaultView> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					<http://localhost:8080/parcels> <https://w3id.org/tree#shape> <http://localhost:8080/parcels/shape> .
					<https://w3id.org/ldes#view> <http://localhost:8080/parcels/pagination> ""^^<http://www.w3.org/2001/XMLSchema#integer> .
					_:genid2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Fragmentation> .
					_:genid2 <http://example.org/name> "pagination" .
					_:genid2 <http://example.org/memberLimit> "100" .
					_:genid1 <http://example.org/fragmentationStrategy> _:genid2 .
					<http://localhost:8080/parcels/pagination> <https://w3id.org/tree#viewDescription> _:genid1 .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#nodeShape> _:genid3 .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#deactivated> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					_:genid3 <http://www.w3.org/ns/shacl#closed> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					_:genid3 <http://www.w3.org/ns/shacl#propertyShape> _:genid4 .
					""")),
			@Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
					[{"@id":"_:b0","@type":["http://example.org/Fragmentation"],"http://example.org/name":[{"@value":"pagination"}],"http://example.org/memberLimit":[{"@value":"100"}]},{"@id":"_:b1","http://example.org/fragmentationStrategy":[{"@id":"_:b0"}]},{"@id":"_:b2","http://www.w3.org/ns/shacl#closed":[{"@value":true}],"http://www.w3.org/ns/shacl#propertyShape":[{"@id":"_:b3"}]},{"@id":"_:b3"},{"@id":"http://example.org/Fragmentation"},{"@id":"http://localhost:8080/parcels","@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://purl.org/dc/terms/created"}],"https://w3id.org/ldes#versionOfPath":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"http://example.org/memberType":[{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"}],"http://example.org/hasDefaultView":[{"@value":true}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/parcels/shape"}]},{"@id":"http://localhost:8080/parcels/pagination","https://w3id.org/tree#viewDescription":[{"@id":"_:b1"}]},{"@id":"http://localhost:8080/parcels/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"],"http://www.w3.org/ns/shacl#nodeShape":[{"@id":"_:b2"}],"http://www.w3.org/ns/shacl#deactivated":[{"@value":true}]},{"@id":"http://purl.org/dc/terms/created"},{"@id":"http://purl.org/dc/terms/isVersionOf"},{"@id":"http://www.w3.org/ns/shacl#NodeShape"},{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"},{"@id":"https://w3id.org/ldes#EventStream"},{"@id":"https://w3id.org/ldes#view","http://localhost:8080/parcels/pagination":[{"@value":"","@type":"http://www.w3.org/2001/XMLSchema#integer"}]}]
										"""))
	})
	@ApiResponse(responseCode = "404", description = "Event Stream with provided collection name could not be found")
	EventStreamResponse getEventStream(@Parameter(example = "parcels") String collectionName);

	@Operation(summary = "Delete an Event Stream")
	@ApiResponse(responseCode = "200", description = "Event Stream has been successfully deleted")
	@ApiResponse(responseCode = "404", description = "Event Stream with provided collection name could not be found")
	void deleteEventStream(@Parameter(example = "parcels") String collectionName);
}
