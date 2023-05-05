package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesConfigShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ViewSpecificationConverter.modelFromView;
import static be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ViewSpecificationConverter.viewFromModel;

@RestController
@RequestMapping("/admin/api/v1")
public class AdminViewsRestController {
	private final ViewService viewService;
	private final LdesConfigShaclValidator viewValidator;

	@Autowired
	public AdminViewsRestController(ViewService viewService,
			@Qualifier("viewShaclValidator") LdesConfigShaclValidator viewValidator) {
		this.viewService = viewService;
		this.viewValidator = viewValidator;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(viewValidator);
	}

	@GetMapping("/eventstreams/{collectionName}/views")
	public ResponseEntity<List<Model>> getViews(@PathVariable String collectionName) {
		return ResponseEntity.ok(viewService.getViewsByCollectionName(collectionName).stream()
				.map(ViewSpecificationConverter::modelFromView)
				.toList());
	}

	@PutMapping("/eventstreams/{collectionName}/views")
	public ResponseEntity<Object> putViews(@PathVariable String collectionName,
			@RequestBody @Validated Model view) {
		viewService.addView(viewFromModel(view, collectionName));
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/eventstreams/{collectionName}/views/{viewName}")
	public ResponseEntity<Object> deleteView(@PathVariable String collectionName, @PathVariable String viewName) {
		viewService.deleteViewByViewName(new ViewName(collectionName, viewName));
		return ResponseEntity.ok().build();
	}

	@GetMapping("/eventstreams/{collectionName}/views/{viewName}")
	public ResponseEntity<Model> getView(@PathVariable String collectionName, @PathVariable String viewName) {
		ViewSpecification view = viewService.getViewByViewName(new ViewName(collectionName, viewName));
		return ResponseEntity.ok(modelFromView(view));
	}
}
