package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.dtos.LdesConfigModelListDto;
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
public class AdminConfigModelsRestController {
	private final LdesConfigModelService service;

	@Autowired
	@Qualifier("configShaclValidator")
	private LdesConfigShaclValidator streamValidator;

	@Autowired
	public AdminConfigModelsRestController(LdesConfigModelService service) {
		this.service = service;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(streamValidator);
	}

	@GetMapping("/eventstreams")
	public ResponseEntity<LdesConfigModelListDto> retrieveAllConfigModels() {
		return ResponseEntity
				.ok(new LdesConfigModelListDto(service.retrieveAllConfigModels()));
	}

	@PutMapping("/eventstreams")
	public ResponseEntity<LdesConfigModel> putConfigModel(@RequestBody @Validated LdesConfigModel ldesConfigModel) {
		return ResponseEntity.ok(service.updateConfigModel(ldesConfigModel));
	}

	@GetMapping("/eventstreams/{collectionName}")
	public ResponseEntity<LdesConfigModel> getConfigModel(@PathVariable String collectionName) {
		return ResponseEntity.ok(service.retrieveConfigModel(collectionName));
	}

	@DeleteMapping("/eventstreams/{collectionName}")
	public ResponseEntity<Object> deleteConfigModel(@PathVariable String collectionName) {
		service.deleteConfigModel(collectionName);
		return ResponseEntity.ok().build();
	}

}
