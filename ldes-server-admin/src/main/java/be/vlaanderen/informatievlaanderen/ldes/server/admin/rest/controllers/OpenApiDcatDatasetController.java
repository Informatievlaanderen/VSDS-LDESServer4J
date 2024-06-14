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

@Tag(name = "DCAT Dataset")
public interface OpenApiDcatDatasetController {

	@ApiResponse(responseCode = "201")
	@ApiResponse(responseCode = "400")
	@Operation(summary = "Add DCAT metadata for a LDES")
	void postDataset(
			@Parameter(description = "The name of a collection", example = "event-stream") String collectionName,
			@RequestBody(description = "The body must contain a blank node of type dcat:Dataset " +
					"with only dcat:Dataset properties (e.g. dct:title) and relations (e.g. dct:creator), " +
					"excluding relations (non-configurable metadata) to dcat:Catalog and dcat:DataService.", content = {
							@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = {
									@ExampleObject(value = """
											@prefix dcat: <http://www.w3.org/ns/dcat#> .
											@prefix dc: <http://purl.org/dc/terms/> .
											[] a dcat:Dataset ;
											   dc:title "My LDES"@en ;
											   dc:description "LDES for my data collection"@en .
											""")
							}),
							@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = {
									@ExampleObject(value = """
											_:genid1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#Dataset> .
											_:genid1 <http://purl.org/dc/terms/title> "My LDES"@en .
											_:genid1 <http://purl.org/dc/terms/description> "LDES for my data collection"@en .
											""")
							}),
							@Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = {
									@ExampleObject(value = """
											[{"@type":["http://www.w3.org/ns/dcat#Dataset"],"http://purl.org/dc/terms/title":[{"@value":"My LDES","@language":"en"}],"http://purl.org/dc/terms/description":[{"@value":"LDES for my data collection","@language":"en"}]},{"@id":"http://www.w3.org/ns/dcat#Dataset"}]
											""")
							})
					}) Model datasetModel);

	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "400")
	@ApiResponse(responseCode = "404")
	@Operation(summary = "Update DCAT metadata for a LDES")
	void putDataset(
			@Parameter(description = "The name of a collection", example = "event-stream") String collectionName,
			@RequestBody(description = "The body must contain a blank node of type dcat:Dataset " +
					"with only dcat:Dataset properties (e.g. dct:title) and relations (e.g. dct:creator), " +
					"excluding relations (non-configurable metadata) to dcat:Catalog and dcat:DataService.", content = {
							@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = {
									@ExampleObject(value = """
											@prefix dcat: <http://www.w3.org/ns/dcat#> .
											@prefix dc: <http://purl.org/dc/terms/> .
											[] a dcat:Dataset ;
											   dc:title "My LDES"@en ;
											   dc:description "LDES for my data collection"@en .
											""")
							}),
							@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = {
									@ExampleObject(value = """
											_:genid1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#Dataset> .
											_:genid1 <http://purl.org/dc/terms/title> "My LDES"@en .
											_:genid1 <http://purl.org/dc/terms/description> "LDES for my data collection"@en .
											""")
							}),
							@Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = {
									@ExampleObject(value = """
											[{"@type":["http://www.w3.org/ns/dcat#Dataset"],"http://purl.org/dc/terms/title":[{"@value":"My LDES","@language":"en"}],"http://purl.org/dc/terms/description":[{"@value":"LDES for my data collection","@language":"en"}]},{"@id":"http://www.w3.org/ns/dcat#Dataset"}]
											""")
							})
					}) Model datasetModel);

	@ApiResponse(responseCode = "200")
	@Operation(summary = "Delete DCAT metadata for a LDES")
	void deleteDataset(
			@Parameter(description = "The name of the collection", example = "event-stream") String collectionName);
}
