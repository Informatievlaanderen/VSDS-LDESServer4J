package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.ViewValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.apache.jena.riot.WebContent.*;

@RestController
@RequestMapping("/admin/api/v1")
public class AdminViewsRestController implements OpenApiAdminViewsRestController {

	private static final Logger log = LoggerFactory.getLogger(AdminViewsRestController.class);

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

	@GetMapping(value = "/eventstreams/{collectionName}/views", produces = { contentTypeJSONLD, contentTypeNQuads,
			contentTypeTurtle })
	public List<ViewSpecification> getViews(@PathVariable String collectionName) {
		return viewService.getViewsByCollectionName(collectionName);
	}

	@PostMapping(value = "/eventstreams/{collectionName}/views", consumes = { contentTypeJSONLD, contentTypeNQuads,
			contentTypeTurtle })
	public void createView(@PathVariable String collectionName,
			@RequestBody @Validated Model view) {
		viewService.addView(viewConverter.viewFromModel(view, collectionName));
	}

	@DeleteMapping("/eventstreams/{collectionName}/views/{view}")
	public void deleteView(@PathVariable String collectionName, @PathVariable String view) {
		ViewName viewName = new ViewName(collectionName, view);
		log.atInfo().log("START deleting " + viewName.asString());
		viewService.deleteViewByViewName(viewName);
		log.atInfo().log("DONE deleting " + viewName.asString());
	}

	@GetMapping(value = "/eventstreams/{collectionName}/views/{viewName}", produces = { contentTypeJSONLD,
			contentTypeNQuads,
			contentTypeTurtle })
	public ViewSpecification getViewOfCollection(@PathVariable String collectionName,
			@PathVariable String viewName) {
		return viewService.getViewByViewName(new ViewName(collectionName, viewName));
	}
}
