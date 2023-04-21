package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesConfigShaclValidator;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1")
public class AdminShapeRestController {

	private final LdesConfigShaclValidator shapeValidator;
	private final ShaclShapeService shaclShapeService;

	public AdminShapeRestController(@Qualifier("shapeShaclValidator") LdesConfigShaclValidator shapeValidator,
			ShaclShapeService shaclShapeService) {
		this.shapeValidator = shapeValidator;
		this.shaclShapeService = shaclShapeService;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(shapeValidator);
	}

	@GetMapping("/eventstreams/{collectionName}/shape")
	public ResponseEntity<Model> getShape(@PathVariable String collectionName) {
		ShaclShape shape = shaclShapeService.retrieveShaclShape(collectionName);
		return ResponseEntity.ok(shape.getModel());
	}

	@PutMapping("/eventstreams/{collectionName}/shape")
	public ResponseEntity<Model> putShape(@PathVariable String collectionName,
			@RequestBody @Validated Model shape) {
		shaclShapeService.updateShaclShape(new ShaclShape(collectionName, shape));
		return ResponseEntity.ok(shape);
	}

}
