package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
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

@Tag(name = "Views")
@SuppressWarnings("java:S2479") // whitespace needed for examples
public interface OpenApiAdminViewsRestController {

	@Operation(summary = "Retrieve a list of configured views of a collection")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeTurtle),
			@Content(mediaType = contentTypeJSONLD),
			@Content(mediaType = contentTypeNQuads) })
	@ApiResponse(responseCode = "404", description = "Missing EventStream", content = @Content)
	List<ViewSpecification> getViews(
			@Parameter(description = "The name of the collection", example = "mobility-hindrances") String collectionName);

	@Operation(summary = "Retrieve a specific view of a collection")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeTurtle),
			@Content(mediaType = contentTypeJSONLD),
			@Content(mediaType = contentTypeNQuads) })
	@ApiResponse(responseCode = "404", description = "Missing EventStream or Missing View", content = @Content)
	ViewSpecification getViewOfCollection(
			@Parameter(description = "The name of the collection", example = "mobility-hindrances") String collectionName,
			@Parameter(description = "The name of requested view", example = "time-based-retention") String viewName);

	@Operation(summary = "Delete a specific view for a collection")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeTurtle),
			@Content(mediaType = contentTypeJSONLD),
			@Content(mediaType = contentTypeNQuads) })
	@ApiResponse(responseCode = "404", description = "Missing EventStream or Missing View")
	void deleteView(
			@Parameter(description = "The name of the collection", example = "mobility-hindrances") String collectionName,
			@Parameter(description = "The name of deleted view", example = "time-based-retention") String viewName);

	@ApiResponse(responseCode = "201")
	@ApiResponse(responseCode = "400", description = "Duplicate View or Wrongly Configured View")
	@ApiResponse(responseCode = "404", description = "Missing EventStream")
	@Operation(summary = "Add view to a collection")
	void createView(
			@Parameter(description = "The name of the collection", example = "mobility-hindrances") String collectionName,
			@RequestBody(description = "A valid RDF model defining a view for a collection", content = {
					@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = {
							@ExampleObject(name = "Time-Based Retention", description = "A time-based retention policy which is configured to only keep members whose ldes:timestamppath is less than 2 minutes ago.", value = """
									@prefix ldes: <https://w3id.org/ldes#> .
									@prefix tree: <https://w3id.org/tree#> .
									@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
									@prefix server: <http://localhost:8080/mobility-hindrances/> .

									server:time-based-retention tree:viewDescription [
										a tree:fragmentationStrategy;
										tree:fragmentationStrategy  () ;
									    tree:pageSize "100"^^<http://www.w3.org/2001/XMLSchema#int> ;
									    ldes:retentionPolicy [
									        a ldes:DurationAgoPolicy  ;
									        tree:value "PT2M"^^xsd:duration ;
									    ] ;
									] .
									"""),
							@ExampleObject(name = "Version-Based Retention", description = "A version-based retention policy which is configured to only keep members the two most recent versions of a resource.", value = """
									@prefix ldes: <https://w3id.org/ldes#> .
									@prefix tree: <https://w3id.org/tree#>.
									@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
									@prefix server: <http://localhost:8080/mobility-hindrances/> .

									server:version-based-retention tree:viewDescription [
										tree:fragmentationStrategy  () ;
									    tree:pageSize "100"^^<http://www.w3.org/2001/XMLSchema#int> ;
									    ldes:retentionPolicy [
									        a ldes:LatestVersionSubset;
									        ldes:amount 2 ;
									    ] ;
									] .
									"""),
							@ExampleObject(name = "Point-In-Time Retention", description = "A point-in-time retention policy which is configured to only keep members whose ldes:timestamppath is after April 12, 2023.", value = """
									@prefix ldes: <https://w3id.org/ldes#> .
									@prefix tree: <https://w3id.org/tree#>.
									@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
									@prefix server: <http://localhost:8080/mobility-hindrances/> .

									server:point-in-time-retention tree:viewDescription [
									    ldes:retentionPolicy [
									        a ldes:PointInTimePolicy ;
									        ldes:pointInTime "2023-04-12T00:00:00"^^xsd:dateTime
									    ] ;
										tree:fragmentationStrategy  () ;
									    tree:pageSize "100"^^<http://www.w3.org/2001/XMLSchema#int> ;
									] .
									"""),
							@ExampleObject(name = "Pagination Fragmentation Strategy", description = "A pagination fragmentation strategy which is configured to create new pages when a member limit of 100 members is reached.", value = """
									@prefix server: <http://localhost:8080/mobility-hindrances/> .
									@prefix tree: <https://w3id.org/tree#> .

									server:pagination tree:viewDescription [
										tree:fragmentationStrategy  () ;
									    tree:pageSize "100"^^<http://www.w3.org/2001/XMLSchema#int> ;
									 ] .
									"""),
							@ExampleObject(name = "Time-Based Fragmentation Strategy", description = "A time-based fragmentation strategy which is configured to create new pages when a member limit of 100 members is reached.", value = """
									@prefix server: <http://localhost:8080/mobility-hindrances/> .
									@prefix tree: <https://w3id.org/tree#> .
									@prefix ldes: <https://w3id.org/ldes#> .

									server:timebased tree:viewDescription [
										tree:fragmentationStrategy ([
											a tree:TimebasedFragmentation ;
									        tree:memberLimit 100 ;
									        tree:fragmentationPath ldes:timestampPath ;
									    ]) ;
									    tree:pageSize "100"^^<http://www.w3.org/2001/XMLSchema#int> ;
									 ] .
									"""),
							@ExampleObject(name = "Geospatial-Pagination Fragmentation Strategy", description = "A combined geospatial-pagination fragmentation strategy which is configured to first partition the data on zoom level 15 and within this zoom level create pages with at most 100 members.", value = """
									@prefix server: <http://localhost:8080/mobility-hindrances/> .
									@prefix tree: <https://w3id.org/tree#> .

									server:geospatial tree:viewDescription [
										tree:fragmentationStrategy ([
											a tree:GeospatialFragmentation ;
											tree:maxZoom 15 ;
											tree:fragmentationPath <http://www.opengis.net/ont/geosparql#asWKT>
										]) ;
										tree:pageSize "100"^^<http://www.w3.org/2001/XMLSchema#int> ;
									] .
									""")
					})
			}) Model view);
}
