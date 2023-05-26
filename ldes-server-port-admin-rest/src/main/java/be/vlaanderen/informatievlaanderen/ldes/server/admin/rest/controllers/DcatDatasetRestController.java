package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services.DcatDatasetService;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin/api/v1/eventstreams/{collectionName}/dcat")
public class DcatDatasetRestController {

	private DcatDatasetService datasetService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void postDataset(@PathVariable String collectionName, @RequestBody Model datasetModel) {
		datasetService.saveDataset(new DcatDataset(collectionName, datasetModel));
	}
}
