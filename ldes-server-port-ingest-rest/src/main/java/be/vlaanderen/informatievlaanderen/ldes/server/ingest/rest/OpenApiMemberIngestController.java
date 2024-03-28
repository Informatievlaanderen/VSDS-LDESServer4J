package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.ResponseEntity;

import static org.apache.jena.riot.WebContent.*;

@Tag(name = "Ingest")
public interface OpenApiMemberIngestController {
    @Operation(summary = "Ingest version object to collection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member was ingested."),
            @ApiResponse(responseCode = "200", description = "Member with the same ID was found, this duplicate member is ignored.")
    })
    ResponseEntity<Object> ingestLdesMember(
            @RequestBody(content = {
                    @Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
                            @prefix schema: <http://schema.org/> .
                            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                            @prefix terms: <http://purl.org/dc/terms/> .
                            @prefix prov:     <http://www.w3.org/ns/prov#> .
                            
                            <https://example.com/John-Doe/1>
                              a schema:Person ;
                              schema:age 16 ;
                              schema:name "John"^^xsd:string, "Johnny"^^xsd:string ;
                              terms:isVersionOf <https://example.com/John-Doe> ;
                              prov:generatedAtTime "2023-11-30T21:45:15+01:00"^^xsd:dateTime .
                            """)),
                    @Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
                            <https://example.com/John-Doe/1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://schema.org/Person> .
                            <https://example.com/John-Doe/1> <http://schema.org/age> "16"^^<http://www.w3.org/2001/XMLSchema#integer> .
                            <https://example.com/John-Doe/1> <http://schema.org/name> "John"^^<http://www.w3.org/2001/XMLSchema#string> .
                            <https://example.com/John-Doe/1> <http://schema.org/name> "Johnny"^^<http://www.w3.org/2001/XMLSchema#string> .
                            <https://example.com/John-Doe/1> <http://purl.org/dc/terms/isVersionOf> <https://example.com/John-Doe> .
                            <https://example.com/John-Doe/1> <http://www.w3.org/ns/prov#generatedAtTime> "2023-11-30T21:45:15+01:00"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
                            """)),
                    @Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
                            [{"@id":"http://schema.org/Person"},{"@id":"https://example.com/John-Doe"},{"@id":"https://example.com/John-Doe/1","@type":["http://schema.org/Person"],"http://schema.org/age":[{"@value":16}],"http://schema.org/name":[{"@value":"John"},{"@value":"Johnny"}],"http://purl.org/dc/terms/isVersionOf":[{"@id":"https://example.com/John-Doe"}],"http://www.w3.org/ns/prov#generatedAtTime":[{"@value":"2023-11-30T21:45:15+01:00","@type":"http://www.w3.org/2001/XMLSchema#dateTime"}]}]
                            """))}) Model ingestedModel,
            @Parameter(name = "collectionName", example = "event-stream") String collectionName);
}
