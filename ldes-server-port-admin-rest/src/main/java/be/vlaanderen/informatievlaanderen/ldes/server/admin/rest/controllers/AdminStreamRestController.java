package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesConfigShaclValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/v1")
public class AdminStreamRestController {
	private final LdesConfigModelService service;

	@Autowired
	@Qualifier("streamShaclValidator")
	private LdesConfigShaclValidator streamValidator;

	@Autowired
	public AdminStreamRestController(LdesConfigModelService service) {
		this.service = service;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(streamValidator);
	}

	@GetMapping("/eventstreams")
	public ResponseEntity<List<LdesConfigModel>> retrieveAllLdesStreams() {
		return ResponseEntity
				.ok(service.retrieveAllEventStreams());
	}

	@PutMapping("/eventstreams")
	public ResponseEntity<LdesConfigModel> putLdesStream(@RequestBody @Validated LdesConfigModel ldesConfigModel) {
		return ResponseEntity.ok(service.updateEventStream(ldesConfigModel));
	}

	@GetMapping("/eventstreams/{collectionName}")
	public ResponseEntity<LdesConfigModel> getLdesStream(@PathVariable String collectionName) {
		return ResponseEntity.ok(service.retrieveEventStream(collectionName));
	}

	@DeleteMapping("/eventstreams/{collectionName}")
	public ResponseEntity<Object> deleteLdesStream(@PathVariable String collectionName) {
		service.deleteEventStream(collectionName);
		return ResponseEntity.ok().build();
	}

}
