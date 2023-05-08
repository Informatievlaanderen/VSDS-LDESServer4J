package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.ShaclShapeValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.jena.rdf.model.Model;
import org.springframework.web.bind.annotation.*;

import static org.apache.jena.riot.WebContent.*;

@RestController
@RequestMapping("/admin/api/v1/eventstreams/{collectionName}/shape")
@Tag(name = "Shape")
public class AdminShapeRestController {

	private final ShaclShapeValidator shapeValidator;
	private final ShaclShapeService shaclShapeService;

	public AdminShapeRestController(ShaclShapeValidator shapeValidator,
			ShaclShapeService shaclShapeService) {
		this.shapeValidator = shapeValidator;
		this.shaclShapeService = shaclShapeService;
	}

	@GetMapping
	@Operation(summary = "Retrieve the shape for a collection")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeNQuads),
			@Content(mediaType = contentTypeJSONLD),
			@Content(mediaType = contentTypeTurtle)
	})
	public String getShape(@Parameter(hidden = true) @RequestHeader("Accept") String contentType,
			@PathVariable String collectionName) {
		ShaclShape shape = shaclShapeService.retrieveShaclShape(collectionName);
		return ModelConverter.toString(shape.getModel(), contentType);
	}

	@PutMapping(consumes = { contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle })
	@Operation(summary = "Adds a shape to a collection")
	public String putShape(@PathVariable String collectionName,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "A valid RDF model defining the Shape of the collection") @RequestBody String shape,
			@Parameter(hidden = true) @RequestHeader("Content-Type") String contentType) {
		Model shapeModel = ModelConverter.toModel(shape, contentType);
		shapeValidator.validateShape(shapeModel);
		shaclShapeService.updateShaclShape(new ShaclShape(collectionName, shapeModel));
		return shape;
	}

}
