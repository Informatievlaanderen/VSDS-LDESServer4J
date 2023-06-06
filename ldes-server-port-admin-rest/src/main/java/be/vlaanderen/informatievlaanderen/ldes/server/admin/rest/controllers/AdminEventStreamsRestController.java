package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.EventStreamValidator;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.apache.jena.riot.WebContent.*;

@RestController
@RequestMapping(value = "/admin/api/v1/eventstreams")
public class AdminEventStreamsRestController implements OpenApiEventStreamsController {

	private final EventStreamService eventStreamService;
	private final EventStreamResponseConverter eventStreamResponseConverter;
	private final EventStreamValidator eventStreamValidator;

	public AdminEventStreamsRestController(EventStreamService eventStreamService,
			EventStreamValidator eventStreamValidator, EventStreamResponseConverter eventStreamResponseConverter) {
		this.eventStreamService = eventStreamService;
		this.eventStreamValidator = eventStreamValidator;
		this.eventStreamResponseConverter = eventStreamResponseConverter;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(eventStreamValidator);
	}

	@Override
	@GetMapping
	public List<EventStreamResponse> getEventStreams() {
		return eventStreamService.retrieveAllEventStreams();
	}

	@ResponseStatus(value = HttpStatus.CREATED)
	@Override
	@PostMapping(consumes = { contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle })
	public EventStreamResponse createEventStream(@RequestBody @Validated Model eventStreamModel) {
		EventStreamResponse eventStreamResponse = eventStreamResponseConverter.fromModel(eventStreamModel);
		return eventStreamService.createEventStream(eventStreamResponse);
	}

	@Override
	@GetMapping("/{collectionName}")
	public EventStreamResponse getEventStream(@PathVariable String collectionName) {
		return eventStreamService.retrieveEventStream(collectionName);
	}

	@Override
	@DeleteMapping("/{collectionName}")
	public void deleteEventStream(@PathVariable String collectionName) {
		eventStreamService.deleteEventStream(collectionName);
	}

}
