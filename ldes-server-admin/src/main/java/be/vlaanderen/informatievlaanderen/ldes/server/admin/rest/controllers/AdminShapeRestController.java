package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.ModelValidator;
import io.micrometer.observation.annotation.Observed;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import static org.apache.jena.riot.WebContent.*;

@Observed
@RestController
@RequestMapping("/admin/api/v1/eventstreams/{collectionName}/shape")
public class AdminShapeRestController implements OpenApiShapeController {
	private final ModelValidator shapeValidator;
	private final ShaclShapeService shaclShapeService;

	public AdminShapeRestController(@Qualifier("shaclShapeShaclValidator") ModelValidator shapeValidator,
									ShaclShapeService shaclShapeService) {
		this.shapeValidator = shapeValidator;
		this.shaclShapeService = shaclShapeService;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(shapeValidator);
	}

	@Override
	@GetMapping
	public Model getShape(@PathVariable String collectionName) {
		ShaclShape shape = shaclShapeService.retrieveShaclShape(collectionName);
		return shape.getModel();
	}

	@Override
	@PutMapping(consumes = {contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle})
	public Model putShape(@PathVariable String collectionName, @RequestBody @Validated Model shape) {
		shaclShapeService.updateShaclShape(new ShaclShape(collectionName, shape));
		return shape;
	}

}
