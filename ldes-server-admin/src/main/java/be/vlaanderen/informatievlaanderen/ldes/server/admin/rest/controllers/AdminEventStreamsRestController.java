package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.EventStreamValidator;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.apache.jena.riot.WebContent.*;

@RestController
@RequestMapping(value = "/admin/api/v1/eventstreams")
public class AdminEventStreamsRestController implements OpenApiEventStreamsController {

	private static final Logger log = LoggerFactory.getLogger(AdminEventStreamsRestController.class);

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
		log.atInfo().log("START deleting " + collectionName);
		eventStreamService.deleteEventStream(collectionName);
		log.atInfo().log("FINISHED deleting " + collectionName);
	}

}
