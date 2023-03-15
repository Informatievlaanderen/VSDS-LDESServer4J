package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.EventStreamConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.EventStreamFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesStreamValidator;
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

    @Autowired
    private LdesStreamValidator validator;

    @Autowired
    public AdminRestController(LdesStreamMongoRepository repository, EventStreamFactory factory) {
        this.repository = repository;
        this.factory = factory;
    }

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }


    @PostMapping(value = "${ldes.collectionname}")
    public void getEventStreams() {

    }


    @GetMapping("/eventstreams")
    public ResponseEntity<List<EventStream>> retrieveAllEventStreams() {
        return ResponseEntity
                .ok()
                .body(repository.retrieveAllEventStreams());
    }

    @PutMapping("/eventstreams")
    public ResponseEntity<EventStream> putEventStream(@RequestBody @Validated EventStream eventStream) {
        repository.saveEventStream(eventStream);
        return ResponseEntity.ok(eventStream);
    }

    @GetMapping("/eventstreams/{collectionName}")
    public ResponseEntity<EventStream> getEventStream(@PathVariable String collectionName) {
        Optional<EventStream> optionalEventStream = repository.retrieveEventStream(collectionName);

        return optionalEventStream.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/eventstreams/{collectionName}/shape")
    public ResponseEntity<String> getShape(@PathVariable String collectionName) {
        String shape = repository.retrieveShape(collectionName);

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
