package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.services.DcatDatasetService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.dcat.DcatDatasetValidator;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.DcatDatasetRestController.BASE_URL;
import static org.apache.jena.riot.WebContent.*;

@RestController
@RequestMapping(value = BASE_URL)
public class DcatDatasetRestController implements OpenApiDcatDatasetController {
	public static final String BASE_URL = "/admin/api/v1/eventstreams/{collectionName}/dcat";
	private final DcatDatasetService datasetService;
	private final DcatDatasetValidator validator;

	public DcatDatasetRestController(DcatDatasetService datasetService, DcatDatasetValidator validator) {
		this.datasetService = datasetService;
		this.validator = validator;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@PostMapping(consumes = { contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle })
	@ResponseStatus(HttpStatus.CREATED)
	public void postDataset(@PathVariable String collectionName, @RequestBody @Validated Model datasetModel) {
		datasetService.saveDataset(new DcatDataset(collectionName, datasetModel));
	}

	@PutMapping(consumes = { contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle })
	@ResponseStatus(HttpStatus.OK)
	public void putDataset(@PathVariable String collectionName, @RequestBody @Validated Model datasetModel) {
		datasetService.updateDataset(new DcatDataset(collectionName, datasetModel));
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.OK)
	public void deleteDataset(@PathVariable String collectionName) {
		datasetService.deleteDataset(collectionName);
	}
}
