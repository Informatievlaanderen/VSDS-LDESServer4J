package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.LdesConfigModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesConfigShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.OpenAPIConfig.*;

@RestController
@RequestMapping("/admin/api/v1")
@Tag(name = "View")
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

	@GetMapping("/eventstreams/{collectionName}/views")
	@Operation(summary = "Retrieve a list of configured views for a collection")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = "application/json")
	})
	public ResponseEntity<List<ViewSpecification>> getViews(@PathVariable String collectionName) {
		return ResponseEntity.ok(List.of());
	}

	@PutMapping(value = "/eventstreams/{collectionName}/views", consumes = { JSON_LD, NQUADS, TURTLE })
	@Operation(summary = "Add a view to a collection")
	public ResponseEntity<String> putViews(@PathVariable String collectionName,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "A valid RDF model defining a view of the collection")
			@RequestBody String view,
			@Parameter(hidden = true) @RequestHeader("Content-Type") String contentType) {
		LdesConfigModel viewModel = LdesConfigModelConverter.toModel(view, contentType);
		viewValidator.validateShape(viewModel.getModel());
		service.addView(collectionName, viewModel);
		return ResponseEntity.ok(view);
	}

	@DeleteMapping("/eventstreams/{collectionName}/views/{viewName}")
	@Operation(summary = "Delete a specific view for a collection")
	public ResponseEntity<Void> deleteView(@PathVariable String collectionName, @PathVariable String viewName) {
		service.deleteView(collectionName, viewName);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/eventstreams/{collectionName}/views/{viewName}")
	@Operation(summary = "Retrieve a specific view config for a collection")
	public ResponseEntity<String> getView(@PathVariable String collectionName, @PathVariable String viewName,
			@Parameter(hidden = true) @RequestHeader("Accept") String contentType) {
		LdesConfigModel configModel = service.retrieveView(collectionName, viewName);
		return ResponseEntity.ok(LdesConfigModelConverter.toString(configModel, contentType));
	}
}
