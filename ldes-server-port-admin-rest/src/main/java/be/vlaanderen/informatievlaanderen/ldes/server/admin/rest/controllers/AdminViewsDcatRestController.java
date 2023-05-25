package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

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

import static org.apache.jena.riot.WebContent.contentTypeJSONLD;
import static org.apache.jena.riot.WebContent.contentTypeNQuads;
import static org.apache.jena.riot.WebContent.contentTypeTurtle;

@Tag(name = "Views DCAT")
public interface AdminViewsDcatRestController {

    @ApiResponses({
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400")
    })
    @Operation(summary = "Add DCAT metadata for a view")
    void createDcat(
            @Parameter(description = "The name of the collection", example = "mobility-hindrances") String collectionName,
            @Parameter(description = "The name of the view", example = "by-page") String viewName,
            @RequestBody(description = "A blank node of type dcat:DataService with only dcat:DataService properties " +
                    "(e.g. dct:title) and relations (e.g. dct:license), excluding relations " +
                    "(non-configurable metadata) to dcat:Catalog and dcat:DataService", content = {
                    @Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(value = """
                                    @prefix dct:   <http://purl.org/dc/terms/> .
                                    @prefix dcat:  <http://www.w3.org/ns/dcat#> .

                                    [] a dcat:DataService ;
                                      dct:title "My geo-spatial view"@en ;
                                      dct:description "Geospatial fragmentation for my LDES"@en .
                                    """)}),
                    @Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(value = """
                                    _:genid1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#DataService> .
                                    _:genid1 <http://purl.org/dc/terms/title> "My geo-spatial view"@en .
                                    _:genid1 <http://purl.org/dc/terms/description> "Geospatial fragmentation for my LDES"@en .
                                    """)}),
                    @Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(value = """
                                    [{"@type":["http://www.w3.org/ns/dcat#DataService"],"http://purl.org/dc/terms/title":[{"@value":"My geo-spatial view","@language":"en"}],"http://purl.org/dc/terms/description":[{"@value":"Geospatial fragmentation for my LDES","@language":"en"}]},{"@id":"http://www.w3.org/ns/dcat#DataService"}]
                                    							""")})
            }) Model dcat);

    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    })
    @Operation(summary = "Update DCAT metadata for a view")
    void updateDcat(
            @Parameter(description = "The name of the collection", example = "mobility-hindrances") String collectionName,
            @Parameter(description = "The name of the view", example = "by-page") String viewName,
            @RequestBody(description = "A blank node of type dcat:DataService with only dcat:DataService properties " +
                    "(e.g. dct:title) and relations (e.g. dct:license), excluding relations " +
                    "(non-configurable metadata) to dcat:Catalog and dcat:DataService", content = {
                    @Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(value = """
                                    @prefix dct:   <http://purl.org/dc/terms/> .
                                    @prefix dcat:  <http://www.w3.org/ns/dcat#> .

                                    [] a dcat:DataService ;
                                      dct:title "My geo-spatial view"@en ;
                                      dct:description "Geospatial fragmentation for my LDES"@en .
                                    """)}),
                    @Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(value = """
                                    _:genid1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#DataService> .
                                    _:genid1 <http://purl.org/dc/terms/title> "My geo-spatial view"@en .
                                    _:genid1 <http://purl.org/dc/terms/description> "Geospatial fragmentation for my LDES"@en .
                                    """)}),
                    @Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = {
                            @ExampleObject(value = """
                                    [{"@type":["http://www.w3.org/ns/dcat#DataService"],"http://purl.org/dc/terms/title":[{"@value":"My geo-spatial view","@language":"en"}],"http://purl.org/dc/terms/description":[{"@value":"Geospatial fragmentation for my LDES","@language":"en"}]},{"@id":"http://www.w3.org/ns/dcat#DataService"}]
                                    							""")})
            }) Model dcat);

    @ApiResponses({
            @ApiResponse(responseCode = "200")
    })
    @Operation(summary = "Delete DCAT metadata for a view")
    void deleteDcat(@Parameter(description = "The name of the collection", example = "mobility-hindrances") String collectionName,
                    @Parameter(description = "The name of the view", example = "by-page") String viewName);


}
