package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.jena.rdf.model.Model;

import static org.apache.jena.riot.WebContent.*;

@Tag(name = "Server")
@Tag(name = "DCAT")
public interface OpenApiServerDcatController {
	@ApiResponse(responseCode = "201", description = "The generated UUID that is linked to the created DCAT", content = @Content(mediaType = contentTypeTextPlain, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "e1c9443a-ab9f-407f-a65b-09d69f481966")))
	@ApiResponse(responseCode = "400", content = @Content, description = "Provided DCAT configuration is not valid")
	@Operation(summary = "Add DCAT configuration for the server")
	String postServerDcat(
			@RequestBody(description = "A blank node of type dcat:Catalog with only dcat:Catalog properties " +
					"(e.g. dct:title) and relations (e.g. dct:publisher), excluding relations " +
					"(non-configurable metadata) to dcat:Dataset and dcat:DataService", content = {
							@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = Model.class), examples = {
									@ExampleObject(value = """
											@prefix dct:   <http://purl.org/dc/terms/> .
											@prefix dcat:  <http://www.w3.org/ns/dcat#> .

											[] a dcat:Catalog ;
											  dct:title "My LDES'es"@en ;
											  dct:description "All LDES'es from publiser X"@en .
											""")
							}),
							@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = {
									@ExampleObject(value = """
											_:genid1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#Catalog> .
											_:genid1 <http://purl.org/dc/terms/title> "My LDES'es"@en .
											_:genid1 <http://purl.org/dc/terms/description> "All LDES'es from publiser X"@en .
											""")
							}),
							@Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = {
									@ExampleObject(value = """
											[{"@type":["http://www.w3.org/ns/dcat#Catalog"],
											"http://purl.org/dc/terms/title":[{"@value":"My LDES'es","@language":"en"}],
											"http://purl.org/dc/terms/description":[{"@value":"All LDES'es from publiser X","@language":"en"}]},{"@id":"http://www.w3.org/ns/dcat#Catalog"}]
											""")
							})
					}) Model dcat);

	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "400", description = "Provided DCAT configuration is not valid")
	@ApiResponse(responseCode = "404", description = "No DCAT configuration found with provided id")
	@Operation(summary = "Update DCAT configuration for the server")
	void putServerDcat(
			@Parameter(name = "catalogId", content = @Content(schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "e1c9443a-ab9f-407f-a65b-09d69f481966"))) String catalogId,
			@RequestBody(description = "A blank node of type dcat:Catalog with only dcat:Catalog properties " +
					"(e.g. dct:title) and relations (e.g. dct:publisher), excluding relations " +
					"(non-configurable metadata) to dcat:Dataset and dcat:DataService", content = {
							@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = Model.class), examples = {
									@ExampleObject(value = """
											@prefix dct:   <http://purl.org/dc/terms/> .
											@prefix dcat:  <http://www.w3.org/ns/dcat#> .

											[] a dcat:Catalog ;
											  dct:title "My LDES'es"@en ;
											  dct:description "All LDES'es from publiser X"@en .
											""")
							}),
							@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = {
									@ExampleObject(value = """
											_:genid1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#Catalog> .
											_:genid1 <http://purl.org/dc/terms/title> "My LDES'es"@en .
											_:genid1 <http://purl.org/dc/terms/description> "All LDES'es from publiser X"@en .
											""")
							}),
							@Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = {
									@ExampleObject(value = """
											[{"@type":["http://www.w3.org/ns/dcat#Catalog"],"http://purl.org/dc/terms/title":[{"@value":"My LDES'es","@language":"en"}],"http://purl.org/dc/terms/description":[{"@value":"All LDES'es from publiser X","@language":"en"}]},{"@id":"http://www.w3.org/ns/dcat#Catalog"}]
											""")
							})
					}) Model dcat);

	@ApiResponse(responseCode = "200")
	@Operation(summary = "Delete DCAT configuration for the server")
	void deleteServerDcat(
			@Parameter(name = "catalogId", content = @Content(schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "e1c9443a-ab9f-407f-a65b-09d69f481966"))) String catalogId);
}
