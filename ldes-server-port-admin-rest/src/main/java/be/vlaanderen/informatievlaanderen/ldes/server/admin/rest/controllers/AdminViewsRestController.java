package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesConfigShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/v1")
public class AdminViewsRestController {
	private final LdesConfigModelService service;
	// TODO use viewService instead of LdesConfigModelService
	private final ViewService viewService;
	private final LdesConfigShaclValidator viewValidator;

	@Autowired
	public AdminViewsRestController(LdesConfigModelService service, ViewService viewService,
			@Qualifier("viewShaclValidator") LdesConfigShaclValidator viewValidator) {
		this.service = service;
		this.viewService = viewService;
		this.viewValidator = viewValidator;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(viewValidator);
	}

	@GetMapping("/eventstreams/{collectionName}/views")
	public ResponseEntity<List<ViewSpecification>> getViews(@PathVariable String collectionName) {
		return ResponseEntity.ok(List.of());
	}

	@PutMapping("/eventstreams/{collectionName}/views")
	public ResponseEntity<LdesConfigModel> putViews(@PathVariable String collectionName,
			@RequestBody @Validated LdesConfigModel view) {
		return ResponseEntity.ok(service.addView(collectionName, view));
	}

	@DeleteMapping("/eventstreams/{collectionName}/views/{viewName}")
	public ResponseEntity<Object> deleteView(@PathVariable String collectionName, @PathVariable String viewName) {
		service.deleteView(collectionName, viewName);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/eventstreams/{collectionName}/views/{viewName}")
	public ResponseEntity<LdesConfigModel> getView(@PathVariable String collectionName, @PathVariable String viewName) {
		return ResponseEntity.ok(service.retrieveView(collectionName, viewName));
	}
}
