package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.jena.rdf.model.Model;

import static org.apache.jena.riot.WebContent.*;

@Tag(name = "Shapes")
@SuppressWarnings("java:S2479") // whitespace needed for examples
public interface OpenApiShapeController {
	@Operation(summary = "Retrieve the shape for a collection")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					@prefix parcels: <http://localhost:8080/parcels/> .
					@prefix sh:   <http://www.w3.org/ns/shacl#> .

					parcels:shape a sh:NodeShape ;
						sh:nodeShape [
							sh:closed true ;
							sh:propertyShape []
							] ;
						sh:deactivated true .
					""")),
			@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					<http://localhost:8080/parcels/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#nodeShape> _:genid1 .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#deactivated> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					_:genid1 <http://www.w3.org/ns/shacl#closed> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					_:genid1 <http://www.w3.org/ns/shacl#propertyShape> _:genid2 .
					""")),
			@Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
					[{"@id":"_:b0","http://www.w3.org/ns/shacl#closed":[{"@value":true}],"http://www.w3.org/ns/shacl#propertyShape":[{"@id":"_:b1"}]},{"@id":"_:b1"},{"@id":"http://localhost:8080/parcels/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"],"http://www.w3.org/ns/shacl#nodeShape":[{"@id":"_:b0"}],"http://www.w3.org/ns/shacl#deactivated":[{"@value":true}]},{"@id":"http://www.w3.org/ns/shacl#NodeShape"}]
					"""))
	})
	@ApiResponse(responseCode = "404", description = "No event stream with provided id could be found")
	Model getShape(String collectionName);

	@Operation(summary = "Adds a shape to a collection")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					@prefix parcels: <http://localhost:8080/parcels/> .
					@prefix sh:   <http://www.w3.org/ns/shacl#> .

					parcels:shape a sh:NodeShape ;
						sh:nodeShape [
							sh:closed true ;
							sh:propertyShape []
							] ;
						sh:deactivated true .
					""")),
			@Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					<http://localhost:8080/parcels/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#nodeShape> _:genid1 .
					<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#deactivated> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					_:genid1 <http://www.w3.org/ns/shacl#closed> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
					_:genid1 <http://www.w3.org/ns/shacl#propertyShape> _:genid2 .
					""")),
			@Content(mediaType = contentTypeJSONLD, examples = @ExampleObject(value = """
					[{"@id":"_:b0","http://www.w3.org/ns/shacl#closed":[{"@value":true}],"http://www.w3.org/ns/shacl#propertyShape":[{"@id":"_:b1"}]},{"@id":"_:b1"},{"@id":"http://localhost:8080/parcels/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"],"http://www.w3.org/ns/shacl#nodeShape":[{"@id":"_:b0"}],"http://www.w3.org/ns/shacl#deactivated":[{"@value":true}]},{"@id":"http://www.w3.org/ns/shacl#NodeShape"}]
					"""))
	})
	@ApiResponse(responseCode = "400", description = "The provided shacl shape is not valid")
	@ApiResponse(responseCode = "404", description = "No event stream with provided id could be found")
	Model putShape(String collectionName,
			@RequestBody(content = {
					@Content(mediaType = contentTypeTurtle, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
							@prefix parcels: <http://localhost:8080/parcels/> .
							@prefix sh:   <http://www.w3.org/ns/shacl#> .

							parcels:shape a sh:NodeShape ;
								sh:nodeShape [
									sh:closed true ;
									sh:propertyShape []
									] ;
								sh:deactivated true .
							""")),
					@Content(mediaType = contentTypeNQuads, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
							<http://localhost:8080/parcels/shape> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/shacl#NodeShape> .
							<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#nodeShape> _:genid1 .
							<http://localhost:8080/parcels/shape> <http://www.w3.org/ns/shacl#deactivated> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
							_:genid1 <http://www.w3.org/ns/shacl#closed> "true"^^<http://www.w3.org/2001/XMLSchema#boolean> .
							_:genid1 <http://www.w3.org/ns/shacl#propertyShape> _:genid2 .
							""")),
					@Content(mediaType = contentTypeJSONLD, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
							[{"@id":"_:b0","http://www.w3.org/ns/shacl#closed":[{"@value":true}],"http://www.w3.org/ns/shacl#propertyShape":[{"@id":"_:b1"}]},{"@id":"_:b1"},{"@id":"http://localhost:8080/parcels/shape","@type":["http://www.w3.org/ns/shacl#NodeShape"],"http://www.w3.org/ns/shacl#nodeShape":[{"@id":"_:b0"}],"http://www.w3.org/ns/shacl#deactivated":[{"@value":true}]},{"@id":"http://www.w3.org/ns/shacl#NodeShape"}]
							"""))
			}) Model shape);
}
