package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesStreamShaclValidator;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/v1")
public class AdminRestController {

	private final LdesConfigModelService service;

	@Autowired
	@Qualifier("streamShaclValidator")
	private LdesStreamShaclValidator ldesStreamShaclValidator;

	@Autowired
	@Qualifier("viewShaclValidator")
	private LdesStreamShaclValidator ldesViewShaclValidator;

	@Autowired
	public AdminRestController(LdesConfigModelService service) {
		this.service = service;
	}

	@InitBinder("stream")
	private void initStreamBinder(WebDataBinder binder) {
		binder.setValidator(ldesStreamShaclValidator);
	}

	@InitBinder("view")
	private void initViewBinder(WebDataBinder binder) {
		binder.setValidator(ldesViewShaclValidator);
	}

	@GetMapping("/eventstreams")
	public ResponseEntity<List<LdesConfigModel>> retrieveAllLdesStreams() {
		return ResponseEntity
				.ok(service.retrieveAllEventStreams());
	}

	@PutMapping("/eventstreams")
	public ResponseEntity<LdesConfigModel> putLdesStream(@RequestBody @Validated LdesConfigModel ldesConfigModel) {
		return ResponseEntity.ok(ldesConfigModel);
	}

	@GetMapping("/eventstreams/{collectionName}")
	public ResponseEntity<LdesConfigModel> getLdesStream(@PathVariable String collectionName) {
		return ResponseEntity.ok(service.retrieveEventStream(collectionName));
	}

	@GetMapping("/eventstreams/{collectionName}/shape")
	public ResponseEntity<Model> getShape(@PathVariable String collectionName) {
		Model shape = service.retrieveShape(collectionName);
		return ResponseEntity.ok(shape);
	}

	@PutMapping("/eventstreams/{collectionName}/shape")
	public ResponseEntity<LdesConfigModel> putShape(@PathVariable String collectionName,
			@RequestBody LdesConfigModel shape) {
		LdesConfigModel updatedShape = service.updateShape(collectionName, shape);
		return ResponseEntity.ok(updatedShape);
	}

	@GetMapping("/eventstreams/{collectionName}/views")
	public ResponseEntity<List<Model>> getViews(@PathVariable String collectionName) {
		return ResponseEntity.ok(service.retrieveViews(collectionName));
	}

	@PutMapping("/eventstreams/{collectionName}/views")
	public ResponseEntity<LdesConfigModel> putViews(@PathVariable String collectionName,
			@RequestBody LdesConfigModel view) {
		return ResponseEntity.ok(service.addView(collectionName, view));
	}

	@GetMapping("/eventstreams/{collectionName}/views/{viewName}")
	public ResponseEntity<Model> getView(@PathVariable String collectionName, @PathVariable String viewName) {
		return ResponseEntity.ok(service.retrieveView(collectionName, viewName));
	}

}
