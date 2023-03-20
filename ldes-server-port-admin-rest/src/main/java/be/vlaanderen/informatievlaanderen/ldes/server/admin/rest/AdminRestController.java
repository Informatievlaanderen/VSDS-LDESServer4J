package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.EventStreamFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.LdesStreamModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesStreamModel;
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

	private final EventStreamFactory factory;

	private final LdesStreamModelService service;

	@Autowired
	@Qualifier("streamShaclValidator")
	private LdesStreamShaclValidator ldesStreamShaclValidator;

	@Autowired
	@Qualifier("viewShaclValidator")
	private LdesStreamShaclValidator ldesViewShaclValidator;

	@Autowired
	public AdminRestController(EventStreamFactory factory, LdesStreamModelService service) {
		this.factory = factory;
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
	public ResponseEntity<List<LdesStreamModel>> retrieveAllLdesStreams() {
		return ResponseEntity
				.ok(service.retrieveAllEventStreams());
	}

	@PutMapping("/eventstreams")
	public ResponseEntity<LdesStreamModel> putLdesStream(@RequestBody @Validated LdesStreamModel ldesStreamModel) {
		return ResponseEntity.ok(ldesStreamModel);
	}

	@GetMapping("/eventstreams/{collectionName}")
	public ResponseEntity<LdesStreamModel> getLdesStream(@PathVariable String collectionName) {
		return ResponseEntity.ok(service.retrieveEventStream(collectionName));
	}

	@GetMapping("/eventstreams/{collectionName}/shape")
	public ResponseEntity<Model> getShape(@PathVariable String collectionName) {
		Model shape = service.retrieveShape(collectionName);
		return ResponseEntity.ok(shape);
	}

	@PutMapping("/eventstreams/{collectionName}/shape")
	public ResponseEntity<LdesStreamModel> putShape(@PathVariable String collectionName,
			@RequestBody LdesStreamModel shape) {
		LdesStreamModel updatedShape = service.updateShape(collectionName, shape);
		return ResponseEntity.ok(updatedShape);
	}

	@GetMapping("/eventstreams/{collectionName}/views")
	public ResponseEntity<List<Model>> getViews(@PathVariable String collectionName) {
		return ResponseEntity.ok(service.retrieveViews(collectionName));
	}

	@PutMapping("/eventstreams/{collectionName}/views")
	public ResponseEntity<LdesStreamModel> putViews(@PathVariable String collectionName,
			@RequestBody LdesStreamModel view) {
		return ResponseEntity.ok(service.addView(collectionName, view));
	}

	@GetMapping("/eventstreams/{collectionName}/views/{viewName}")
	public ResponseEntity<Model> getView(@PathVariable String collectionName, @PathVariable String viewName) {
		return ResponseEntity.ok(service.retrieveView(collectionName, viewName));
	}

}
