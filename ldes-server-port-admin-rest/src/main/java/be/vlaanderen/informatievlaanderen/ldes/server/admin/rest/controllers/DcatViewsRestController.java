package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.DcatViewValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.DcatViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import static org.apache.jena.riot.WebContent.contentTypeJSONLD;
import static org.apache.jena.riot.WebContent.contentTypeNQuads;
import static org.apache.jena.riot.WebContent.contentTypeTurtle;

@RestController
@RequestMapping(DcatViewsRestController.BASE_URL)
public class DcatViewsRestController implements OpenApiDcatViewsController {

	public final static String BASE_URL = "/admin/api/v1/eventstreams/{collectionName}/views/{viewName}/dcat";

	private final DcatViewService dcatViewService;

	private final DcatViewValidator dcatViewValidator;

	public DcatViewsRestController(DcatViewService dcatViewService, DcatViewValidator dcatViewValidator) {
		this.dcatViewService = dcatViewService;
		this.dcatViewValidator = dcatViewValidator;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(dcatViewValidator);
	}

	@PostMapping(consumes = { contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle })
	@ResponseStatus(HttpStatus.CREATED)
	public void createDcat(@PathVariable String collectionName,
			@PathVariable String viewName,
			@RequestBody @Validated Model dcat) {
		dcatViewService.create(new ViewName(collectionName, viewName), dcat);
	}

	@PutMapping(consumes = { contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle })
	public void updateDcat(@PathVariable String collectionName, @PathVariable String viewName,
			@RequestBody @Validated Model dcat) {
		dcatViewService.update(new ViewName(collectionName, viewName), dcat);
	}

	@DeleteMapping
	public void deleteDcat(@PathVariable String collectionName, @PathVariable String viewName) {
		dcatViewService.delete(new ViewName(collectionName, viewName));
	}

}
