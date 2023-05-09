package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.ViewValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/v1")
public class AdminViewsRestController {
	private final ViewService viewService;
	private final ViewValidator viewValidator;
	private final ViewSpecificationConverter viewConverter;

	public AdminViewsRestController(ViewService viewService, ViewValidator viewValidator,
			ViewSpecificationConverter viewConverter) {
		this.viewService = viewService;
		this.viewValidator = viewValidator;
		this.viewConverter = viewConverter;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(viewValidator);
	}

	@GetMapping("/eventstreams/{collectionName}/views")
	public ResponseEntity<List<ViewSpecification>> getViews(@PathVariable String collectionName) {
		return ResponseEntity.ok(viewService.getViewsByCollectionName(collectionName));
	}

	@PutMapping("/eventstreams/{collectionName}/views")
	public ResponseEntity<Object> putViews(@PathVariable String collectionName,
			@RequestBody @Validated Model view) {
		viewService.addView(viewConverter.viewFromModel(view, collectionName));
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/eventstreams/{collectionName}/views/{viewName}")
	public ResponseEntity<Object> deleteView(@PathVariable String collectionName, @PathVariable String viewName) {
		viewService.deleteViewByViewName(new ViewName(collectionName, viewName));
		return ResponseEntity.ok().build();
	}

	@GetMapping("/eventstreams/{collectionName}/views/{viewName}")
	public ResponseEntity<ViewSpecification> getView(@PathVariable String collectionName,
			@PathVariable String viewName) {
		ViewSpecification view = viewService.getViewByViewName(new ViewName(collectionName, viewName));
		return ResponseEntity.ok(view);
	}
}
