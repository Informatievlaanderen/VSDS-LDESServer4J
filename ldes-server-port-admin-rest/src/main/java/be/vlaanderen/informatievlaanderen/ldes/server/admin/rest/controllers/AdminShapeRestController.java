package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesConfigShaclValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1")
public class AdminShapeRestController {
	private final LdesConfigModelService service;

	@Autowired
	@Qualifier("shapeShaclValidator")
	private LdesConfigShaclValidator shapeValidator;

	@Autowired
	public AdminShapeRestController(LdesConfigModelService service) {
		this.service = service;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(shapeValidator);
	}

	@GetMapping("/eventstreams/{collectionName}/shape")
	public ResponseEntity<LdesConfigModel> getShape(@PathVariable String collectionName) {
		LdesConfigModel shape = service.retrieveShape(collectionName);
		return ResponseEntity.ok(shape);
	}

	@PutMapping("/eventstreams/{collectionName}/shape")
	public ResponseEntity<LdesConfigModel> putShape(@PathVariable String collectionName,
			@RequestBody @Validated LdesConfigModel shape) {
		LdesConfigModel updatedShape = service.updateShape(collectionName, shape);
		return ResponseEntity.ok(updatedShape);
	}

}
