package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.ViewValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.jena.rdf.model.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.apache.jena.riot.WebContent.*;

@RestController
@RequestMapping("/admin/api/v1/eventstreams/{collectionName}/views")
@Tag(name = "Views")
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

	@GetMapping
	@Operation(summary = "Retrieve a list of configured views for a collection")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = "application/json")
	})
	public List<ViewSpecification> getViews(@PathVariable String collectionName) {
		return viewService.getViewsByCollectionName(collectionName);
	}

	@PutMapping(consumes = { contentTypeJSONLD, contentTypeNQuads,
			contentTypeTurtle })
	@Operation(summary = "Add a view to a collection")
	public void putViews(@PathVariable String collectionName,
			@Parameter(schema = @Schema(implementation = String.class), description = "A valid RDF model defining a view for a collection") @RequestBody @Validated Model view) {
		viewService.addView(viewConverter.viewFromModel(view, collectionName));
	}

	@DeleteMapping("/{viewName}")
	@Operation(summary = "Delete a specific view for a collection")
	public void deleteView(@PathVariable String collectionName, @PathVariable String viewName) {
		viewService.deleteViewByViewName(new ViewName(collectionName, viewName));
	}

	@GetMapping("/{viewName}")
	@Operation(summary = "Retrieve a specific view config for a collection")
	public ViewSpecification getView(@PathVariable String collectionName,
			@PathVariable String viewName) {
		return viewService.getViewByViewName(new ViewName(collectionName, viewName));
	}
}
