package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.EventStreamValidator;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/v1/eventstreams")
public class AdminEventStreamsRestController {

	private final EventStreamService eventStreamService;
	private final EventStreamResponseConverter eventStreamResponseConverter = new EventStreamResponseConverter();
	private final EventStreamValidator eventStreamValidator;

	public AdminEventStreamsRestController(EventStreamService eventStreamService,
			EventStreamValidator eventStreamValidator) {
		this.eventStreamService = eventStreamService;
		this.eventStreamValidator = eventStreamValidator;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(eventStreamValidator);
	}

	@GetMapping
	public List<EventStreamResponse> getEventStreams() {
		return eventStreamService.retrieveAllEventStreams();
	}

	@PutMapping
	public EventStreamResponse putEventStream(@RequestBody @Validated Model eventStreamModel) {
		EventStreamResponse eventStreamResponse = eventStreamResponseConverter.fromModel(eventStreamModel);
		return eventStreamService.saveEventStream(eventStreamResponse);
	}

	@GetMapping("/{collectionName}")
	public EventStreamResponse getEventStream(@PathVariable String collectionName) {
		return eventStreamService.retrieveEventStream(collectionName);
	}

	@DeleteMapping("/{collectionName}")
	public ResponseEntity<Object> deleteEventStream(@PathVariable String collectionName) {
		eventStreamService.deleteEventStream(collectionName);
		return ResponseEntity.ok().build();
	}

}
