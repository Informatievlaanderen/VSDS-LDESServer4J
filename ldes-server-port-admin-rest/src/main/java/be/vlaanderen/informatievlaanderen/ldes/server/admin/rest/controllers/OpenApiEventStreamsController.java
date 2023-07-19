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
					@prefix dcterms: <http://purl.org/dc/terms/> .
					@prefix tree: <https://w3id.org/tree#>.
					@prefix sh:   <http://www.w3.org/ns/shacl#> .
					@prefix server: <http://localhost:8080/> .
					@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
					@prefix parcels: <http://localhost:8080/parcels/> .
					@prefix mobility-hindrances: <http://localhost:8080/mobility-hindrances/> .

					server:mobility-hindrances a ldes:EventStream ;
						ldes:timestampPath dcterms:created ;
						ldes:versionOfPath dcterms:isVersionOf ;
						tree:shape mobility-hindrances:shape .

					server:parcels a ldes:EventStream ;
						ldes:timestampPath dcterms:created ;
						ldes:versionOfPath dcterms:isVersionOf ;
						tree:shape server:shape .
						ldes:view parcels:by-page .

					parcels:paged tree:viewDescription [
						tree:fragmentationStrategy [
							a tree:PaginationFragmentation ;
							tree:memberLimit "250"
						] ;
					] .

					parcels:shape a sh:NodeShape ;
						sh:targetClass <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> .

					mobility-hindrances:shape a sh:NodeShape ;
						sh:targetClass <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> .
					""")),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					<http://localhost:8080/mobility-hindrances> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
					<http://localhost:8080/mobility-hindrances> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
					<http://localhost:8080/mobility-hindrances> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
					<http://localhost:8080/mobility-hindrances> <https://w3id.org/tree#shape> <http://localhost:8080/mobility-hindrances/shape> .
					<http://localhost:8080/parcels> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
					<http://localhost:8080/parcels> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
					<http://localhost:8080/parcels> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
					<http://localhost:8080/parcels> <https://w3id.org/tree#shape> <http://localhost:8080/shape> .
					<https://w3id.org/ldes#view> <http://localhost:8080/parcels/by-page> ""^^<http://www.w3.org/2001/XMLSchema#integer> .
					_:genid2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/tree#PaginationFragmentation> .
					_:genid2 <https://w3id.org/tree#memberLimit> "250" .
					_:genid1 <https://w3id.org/tree#fragmentationStrategy> _:genid2 .
					<http://localhost:8080/parcels/paged> <https://w3id.org/tree#viewDescription> _:genid1 .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#targetClass> <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> .
					<http://localhost:8080/mobility-hindrances/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
					<http://localhost:8080/mobility-hindrances/shape> <http://www.w3.org/ns/shacl#targetClass> <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> .
					""")),
			@Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
					[{"@id":"_:b0","@type":["https://w3id.org/tree#PaginationFragmentation"],"https://w3id.org/tree#memberLimit":[{"@value":"250"}]},{"@id":"_:b1","https://w3id.org/tree#fragmentationStrategy":[{"@id":"_:b0"}]},{"@id":"http://localhost:8080/mobility-hindrances","@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://purl.org/dc/terms/created"}],"https://w3id.org/ldes#versionOfPath":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/mobility-hindrances/shape"}]},{"@id":"http://localhost:8080/mobility-hindrances/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"],"http://www.w3.org/ns/shacl#targetClass":[{"@id":"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder"}]},{"@id":"http://localhost:8080/parcels","@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://purl.org/dc/terms/created"}],"https://w3id.org/ldes#versionOfPath":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/shape"}]},{"@id":"http://localhost:8080/parcels/paged","https://w3id.org/tree#viewDescription":[{"@id":"_:b1"}]},{"@id":"http://localhost:8080/parcels/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"],"http://www.w3.org/ns/shacl#targetClass":[{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"}]},{"@id":"http://localhost:8080/shape"},{"@id":"http://purl.org/dc/terms/created"},{"@id":"http://purl.org/dc/terms/isVersionOf"},{"@id":"http://www.w3.org/ns/shacl#NodeShape"},{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"},{"@id":"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder"},{"@id":"https://w3id.org/ldes#EventStream"},{"@id":"https://w3id.org/ldes#view","http://localhost:8080/parcels/by-page":[{"@value":"","@type":"http://www.w3.org/2001/XMLSchema#integer"}]},{"@id":"https://w3id.org/tree#PaginationFragmentation"}]
					"""))
	})
	List<EventStreamResponse> getEventStreams();

	@Operation(summary = "Create an Event Stream based on the provided config")
	@ApiResponse(responseCode = "201", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					@prefix ldes: <https://w3id.org/ldes#> .
					@prefix dcterms: <http://purl.org/dc/terms/> .
					@prefix tree: <https://w3id.org/tree#>.
					@prefix sh:   <http://www.w3.org/ns/shacl#> .
					@prefix server: <http://localhost:8080/> .
					@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
					@prefix parcels: <http://localhost:8080/parcels/> .

					server:parcels a ldes:EventStream ;
						ldes:timestampPath dcterms:created ;
						ldes:versionOfPath dcterms:isVersionOf ;
						tree:shape parcels:shape .

					parcels:shape a sh:NodeShape ;
						sh:targetClass <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> .
					""")),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					<http://localhost:8080/parcels> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
					<http://localhost:8080/parcels> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
					<http://localhost:8080/parcels> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
					<http://localhost:8080/parcels> <https://w3id.org/tree#shape> <http://localhost:8080/parcels/shape> .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#targetClass> <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> .
					""")),
			@Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
					[{"@id":"http://localhost:8080/parcels","@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://purl.org/dc/terms/created"}],"https://w3id.org/ldes#versionOfPath":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/parcels/shape"}]},{"@id":"http://localhost:8080/parcels/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"],"http://www.w3.org/ns/shacl#targetClass":[{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"}]},{"@id":"http://purl.org/dc/terms/created"},{"@id":"http://purl.org/dc/terms/isVersionOf"},{"@id":"http://www.w3.org/ns/shacl#NodeShape"},{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"},{"@id":"https://w3id.org/ldes#EventStream"}]
					"""))
	})
	@ApiResponse(responseCode = "400", description = "The provided config is not valid", content = @Content)
	EventStreamResponse createEventStream(
			@RequestBody(content = {
					@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
							@prefix ldes: <https://w3id.org/ldes#> .
							@prefix dcterms: <http://purl.org/dc/terms/> .
							@prefix tree: <https://w3id.org/tree#>.
							@prefix sh:   <http://www.w3.org/ns/shacl#> .
							@prefix server: <http://localhost:8080/> .
							@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .
							@prefix parcels: <http://localhost:8080/parcels/> .

							server:parcels a ldes:EventStream ;
								ldes:timestampPath dcterms:created ;
								ldes:versionOfPath dcterms:isVersionOf ;
								tree:shape parcels:shape .

							parcels:shape a sh:NodeShape ;
								sh:targetClass <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> .
							""")),
					@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
							<http://localhost:8080/parcels> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
						  	<http://localhost:8080/parcels> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
						  	<http://localhost:8080/parcels> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
						  	<http://localhost:8080/parcels> <https://w3id.org/tree#shape> <http://localhost:8080/parcels/shape> .
						  	<http://localhost:8080/parcels/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
						  	<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#targetClass> <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> .
							""")),
					@Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
							[{"@id":"http://localhost:8080/parcels","@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://purl.org/dc/terms/created"}],"https://w3id.org/ldes#versionOfPath":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/parcels/shape"}]},{"@id":"http://localhost:8080/parcels/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"],"http://www.w3.org/ns/shacl#targetClass":[{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"}]},{"@id":"http://purl.org/dc/terms/created"},{"@id":"http://purl.org/dc/terms/isVersionOf"},{"@id":"http://www.w3.org/ns/shacl#NodeShape"},{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"},{"@id":"https://w3id.org/ldes#EventStream"}]
							"""))
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
					@prefix parcels: <http://localhost:8080/parcels/> .

					server:parcels a ldes:EventStream ;
						ldes:timestampPath dcterms:created ;
						ldes:versionOfPath dcterms:isVersionOf ;
						tree:shape parcels:shape .
						ldes:view parcels:pagination .

					parcels:pagination tree:viewDescription [
						tree:fragmentationStrategy [
							a tree:PaginationFragmentation ;
							tree:memberLimit "100"
						] ;
					] .

					parcels:shape a sh:NodeShape ;
						sh:targetClass <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> .
					""")),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					<http://localhost:8080/parcels> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
					<http://localhost:8080/parcels> <https://w3id.org/ldes#timestampPath> <http://purl.org/dc/terms/created> .
					<http://localhost:8080/parcels> <https://w3id.org/ldes#versionOfPath> <http://purl.org/dc/terms/isVersionOf> .
					<http://localhost:8080/parcels> <https://w3id.org/tree#shape> <http://localhost:8080/parcels/shape> .
					<https://w3id.org/ldes#view> <http://localhost:8080/parcels/pagination> ""^^<http://www.w3.org/2001/XMLSchema#integer> .
					_:genid2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/tree#PaginationFragmentation> .
					_:genid2 <https://w3id.org/tree#memberLimit> "100" .
					_:genid1 <https://w3id.org/tree#fragmentationStrategy> _:genid2 .
					<http://localhost:8080/parcels/pagination> <https://w3id.org/tree#viewDescription> _:genid1 .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#targetClass> <https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel> .
					""")),
			@Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
					[{"@id":"_:b0","@type":["https://w3id.org/tree#PaginationFragmentation"],"https://w3id.org/tree#memberLimit":[{"@value":"100"}]},{"@id":"_:b1","https://w3id.org/tree#fragmentationStrategy":[{"@id":"_:b0"}]},{"@id":"http://localhost:8080/parcels","@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://purl.org/dc/terms/created"}],"https://w3id.org/ldes#versionOfPath":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/parcels/shape"}]},{"@id":"http://localhost:8080/parcels/pagination","https://w3id.org/tree#viewDescription":[{"@id":"_:b1"}]},{"@id":"http://localhost:8080/parcels/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"],"http://www.w3.org/ns/shacl#targetClass":[{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"}]},{"@id":"http://purl.org/dc/terms/created"},{"@id":"http://purl.org/dc/terms/isVersionOf"},{"@id":"http://www.w3.org/ns/shacl#NodeShape"},{"@id":"https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"},{"@id":"https://w3id.org/ldes#EventStream"},{"@id":"https://w3id.org/ldes#view","http://localhost:8080/parcels/pagination":[{"@value":"","@type":"http://www.w3.org/2001/XMLSchema#integer"}]},{"@id":"https://w3id.org/tree#PaginationFragmentation"}]
					"""))
	})
	@ApiResponse(responseCode = "404", description = "Event Stream with provided collection name could not be found", content = @Content)
	EventStreamResponse getEventStream(@Parameter(example = "parcels") String collectionName);

	@Operation(summary = "Delete an Event Stream")
	@ApiResponse(responseCode = "200", description = "Event Stream has been successfully deleted")
	@ApiResponse(responseCode = "404", description = "Event Stream with provided collection name could not be found")
	void deleteEventStream(@Parameter(example = "parcels") String collectionName);
}
