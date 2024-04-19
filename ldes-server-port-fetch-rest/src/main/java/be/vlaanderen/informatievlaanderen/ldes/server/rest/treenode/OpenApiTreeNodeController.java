package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Map;

import static org.apache.jena.riot.WebContent.*;

@Tag(name = "Fetch")
@SuppressWarnings("java:S2479")
public interface OpenApiTreeNodeController {

	@Operation(summary = "Retrieve an LDES Fragment in a streaming way")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE, schema = @Schema(implementation = String.class))
	})
	@ApiResponse(responseCode = "404", content = @Content, description = "No Linked Data Event Stream found with provided collection name")
    ResponseEntity<ResponseBodyEmitter> retrieveLdesFragmentStreaming(@PathVariable("view") String view,
                                                                      @RequestParam Map<String, String> requestParameters,
                                                                      @PathVariable("collectionname") String collectionName);

    @Operation(summary = "Retrieve an LDES Fragment")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					@prefix ns0: <https://w3id.org/tree#> .
					@prefix ns1: <https://w3id.org/ldes#> .
					@prefix prov: <http://www.w3.org/ns/prov#> .
					@prefix dc: <http://purl.org/dc/terms/> .
					@prefix schema: <http://schema.org/> .
					@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
					
					<http://localhost:8080/event-stream>
					  ns0:member <https://localhost:8080/event-stream/John-Doe/1> ;
					  a <https://w3id.org/ldes#EventStream> ;
					  ns1:timestampPath prov:generatedAtTime ;
					  ns1:versionOf dc:isVersionOf ;
					  ns0:shape <http://localhost:8080/event-stream/shape> ;
					  ns0:view <http://localhost:8080/event-stream?fragment=1> .
					
					<https://localhost:8080/event-stream/John-Doe/1>
					  a schema:Person ;
					  schema:age 16 ;
					  schema:name "John"^^xsd:string, "Johnny"^^xsd:string ;
					  dc:isVersionOf <https://example.com/John-Doe> ;
					  prov:generatedAtTime "2023-11-30T21:45:15+01:00"^^xsd:dateTime .
					
					<http://localhost:8080/exampleData?fragment=1> a ns0:Node .
					""")),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
				<http://localhost:8080/event-stream> <https://w3id.org/tree#member> <https://localhost:8080/event-stream/John-Doe/1> .
				<http://localhost:8080/event-stream> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .
				<http://localhost:8080/event-stream> <https://w3id.org/ldes#timestampPath> <http://www.w3.org/ns/prov#generatedAtTime> .
				<http://localhost:8080/event-stream> <https://w3id.org/ldes#versionOf> <http://purl.org/dc/terms/isVersionOf> .
				<http://localhost:8080/event-stream> <https://w3id.org/tree#shape> <http://localhost:8080/event-stream/shape> .
				<http://localhost:8080/event-stream> <https://w3id.org/tree#view> <http://localhost:8080/event-stream?fragment=1> .
				<https://localhost:8080/event-stream/John-Doe/1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://schema.org/Person> .
				<https://localhost:8080/event-stream/John-Doe/1> <http://schema.org/age> "16"^^<http://www.w3.org/2001/XMLSchema#integer> .
				<https://localhost:8080/event-stream/John-Doe/1> <http://schema.org/name> "John"^^<http://www.w3.org/2001/XMLSchema#string> .
				<https://localhost:8080/event-stream/John-Doe/1> <http://schema.org/name> "Johnny"^^<http://www.w3.org/2001/XMLSchema#string> .
				<https://localhost:8080/event-stream/John-Doe/1> <http://purl.org/dc/terms/isVersionOf> <https://example.com/John-Doe> .
				<https://localhost:8080/event-stream/John-Doe/1> <http://www.w3.org/ns/prov#generatedAtTime> "2023-11-30T21:45:15+01:00"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
				<http://localhost:8080/exampleData?fragment=1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/tree#Node> .
			""")),
			@Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
					[{"@id":"http://localhost:8080/event-stream","https://w3id.org/tree#member":[{"@id":"https://localhost:8080/event-stream/John-Doe/1"}],"@type":["https://w3id.org/ldes#EventStream"],"https://w3id.org/ldes#timestampPath":[{"@id":"http://www.w3.org/ns/prov#generatedAtTime"}],"https://w3id.org/ldes#versionOf":[{"@id":"http://purl.org/dc/terms/isVersionOf"}],"https://w3id.org/tree#shape":[{"@id":"http://localhost:8080/event-stream/shape"}],"https://w3id.org/tree#view":[{"@id":"http://localhost:8080/event-stream?fragment=1"}]},{"@id":"http://localhost:8080/event-stream/shape"},{"@id":"http://localhost:8080/event-stream?fragment=1"},{"@id":"http://localhost:8080/exampleData?fragment=1","@type":["https://w3id.org/tree#Node"]},{"@id":"http://purl.org/dc/terms/isVersionOf"},{"@id":"http://schema.org/Person"},{"@id":"http://www.w3.org/ns/prov#generatedAtTime"},{"@id":"https://example.com/John-Doe"},{"@id":"https://localhost:8080/event-stream/John-Doe/1","@type":["http://schema.org/Person"],"http://schema.org/age":[{"@value":16}],"http://schema.org/name":[{"@value":"John"},{"@value":"Johnny"}],"http://purl.org/dc/terms/isVersionOf":[{"@id":"https://example.com/John-Doe"}],"http://www.w3.org/ns/prov#generatedAtTime":[{"@value":"2023-11-30T21:45:15+01:00","@type":"http://www.w3.org/2001/XMLSchema#dateTime"}]},{"@id":"https://w3id.org/ldes#EventStream"},{"@id":"https://w3id.org/tree#Node"}]
					""")),
	})
	@ApiResponse(responseCode = "404", content = @Content, description = "No Linked Data Event Stream found with provided collection name")
	ResponseEntity<TreeNode> retrieveLdesFragment(
			@Parameter(example = "by-time") String view,
			@Parameter(examples = @ExampleObject(value = """
					{
						"fragment": "1"
					}
					""")) Map<String, String> requestParameters,
			@Parameter(hidden = true) String language,
			@Parameter(example = "event-stream") String collectionName);
}
