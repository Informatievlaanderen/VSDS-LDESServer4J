package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.EventStreamHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.EventStreamValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.apache.jena.riot.WebContent.*;

@RestController
@RequestMapping(value = "/admin/api/v1/eventstreams")
@Tag(name = "Event Streams")
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
	@Operation(summary = "Retrieve list of configured Event Streams")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeNQuads),
			@Content(mediaType = contentTypeJSONLD),
			@Content(mediaType = contentTypeTurtle)
	})
	public List<EventStreamResponse> getEventStreams() {
		return eventStreamService.retrieveAllEventStreams();
	}

	@PutMapping(consumes = { contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle })
	@Operation(summary = "Create an Event Stream based on the provided config")
	public EventStreamResponse putEventStream(@RequestBody @Validated Model eventStreamModel) {
		EventStreamResponse eventStreamResponse = eventStreamResponseConverter.fromModel(eventStreamModel);
		return eventStreamService.saveEventStream(eventStreamResponse);
	}

	@GetMapping("/{collectionName}")
	@Operation(summary = "Retrieve specific Event Stream configuration")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeNQuads),
			@Content(mediaType = contentTypeJSONLD),
			@Content(mediaType = contentTypeTurtle)
	})
	public EventStreamResponse getEventStream(@PathVariable String collectionName) {
		return eventStreamService.retrieveEventStream(collectionName);
	}

	@DeleteMapping("/{collectionName}")
	@Operation(summary = "Delete an Event Stream")
	public ResponseEntity<Void> deleteEventStream(@PathVariable String collectionName) {
		eventStreamService.deleteEventStream(collectionName);
		return ResponseEntity.ok().build();
	}

}
