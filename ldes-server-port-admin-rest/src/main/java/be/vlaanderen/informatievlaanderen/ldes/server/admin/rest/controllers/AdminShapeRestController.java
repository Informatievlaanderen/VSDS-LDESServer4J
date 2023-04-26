package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.ShaclShapeValidator;
import org.apache.jena.rdf.model.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1")
public class AdminShapeRestController {

	private final ShaclShapeValidator shapeValidator;
	private final ShaclShapeService shaclShapeService;

	public AdminShapeRestController(ShaclShapeValidator shapeValidator,
			ShaclShapeService shaclShapeService) {
		this.shapeValidator = shapeValidator;
		this.shaclShapeService = shaclShapeService;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(shapeValidator);
	}

	@GetMapping("/eventstreams/{collectionName}/shape")
	public Model getShape(@PathVariable String collectionName) {
		ShaclShape shape = shaclShapeService.retrieveShaclShape(collectionName);
		return shape.getModel();
	}

	@PutMapping("/eventstreams/{collectionName}/shape")
	public Model putShape(@PathVariable String collectionName,
			@RequestBody @Validated Model shape) {
		shaclShapeService.updateShaclShape(new ShaclShape(collectionName, shape));
		return shape;
	}

}
