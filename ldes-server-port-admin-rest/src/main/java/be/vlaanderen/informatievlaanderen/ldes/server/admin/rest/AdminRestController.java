package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.EventStreamFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.LdesStreamModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.LdesStreamModelServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesStreamModel;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesStreamShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.LdesStreamMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/api/v1")
public class AdminRestController {

	private final LdesStreamMongoRepository repository;
	private final EventStreamFactory factory;

	private final LdesStreamModelService service;

	@Autowired
	private LdesStreamShaclValidator ldesStreamShaclValidator;

	@Autowired
	public AdminRestController(LdesStreamMongoRepository repository, EventStreamFactory factory,
			LdesStreamModelService service) {
		this.repository = repository;
		this.factory = factory;
		this.service = service;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(ldesStreamShaclValidator);
	}

	@PostMapping(value = "${ldes.collectionname}")
	public void getLdesStreams() {

	}

	@GetMapping("/eventstreams")
	public ResponseEntity<List<LdesStreamModel>> retrieveAllLdesStreams() {
		return ResponseEntity
				.ok()
				.body(repository.retrieveAllLdesStreams());
	}

	@PutMapping("/eventstreams")
	public ResponseEntity<LdesStreamModel> putLdesStream(@RequestBody @Validated LdesStreamModel ldesStreamModel) {
		repository.saveLdesStream(ldesStreamModel);
		return ResponseEntity.ok(ldesStreamModel);
	}

	@GetMapping("/eventstreams/{collectionName}")
	public ResponseEntity<LdesStreamModel> getLdesStream(@PathVariable String collectionName) {
		Optional<LdesStreamModel> optionalLdesStream = repository.retrieveLdesStream(collectionName);

		return optionalLdesStream.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/eventstreams/{collectionName}/shape")
	public ResponseEntity<String> getShape(@PathVariable String collectionName) {
		String shape = service.retrieveShape(collectionName);
		return ResponseEntity.ok(shape);
	}

	@PutMapping("/eventstreams/{collectionName}/shape")
	public ResponseEntity<String> putShape(@PathVariable String collectionName, @RequestBody String shape) {
		String updatedShape = repository.updateShape(collectionName, shape);

		return ResponseEntity.ok(updatedShape);
	}

	@GetMapping("/eventstreams/{collectionName}/views")
	public ResponseEntity<List<TreeNode>> getViews(@PathVariable String collectionName) {
		return ResponseEntity.ok(repository.retrieveViews(collectionName));
	}

	@PutMapping("/eventstreams/{collectionName}/views")
    public ResponseEntity<TreeNode> putViews(@PathVariable String collectionName, @RequestBody TreeNode view) {
        String viewName = repository.addView(collectionName, view.)
    }

}
