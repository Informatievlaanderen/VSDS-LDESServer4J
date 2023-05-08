package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.EventStreamHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
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
	private final ShaclShapeService shaclShapeService;
	private final ViewService viewService;
	private final EventStreamResponseConverter eventStreamResponseConverter = new EventStreamResponseConverter();
	private final EventStreamValidator eventStreamValidator;

	public AdminEventStreamsRestController(EventStreamService eventStreamService, ShaclShapeService shaclShapeService,
			ViewService viewService, EventStreamValidator eventStreamValidator) {
		this.eventStreamService = eventStreamService;
		this.shaclShapeService = shaclShapeService;
		this.viewService = viewService;
		this.eventStreamValidator = eventStreamValidator;
	}

	@GetMapping
	@Operation(summary = "Retrieve list of configured Event Streams")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeNQuads),
			@Content(mediaType = contentTypeJSONLD),
			@Content(mediaType = contentTypeTurtle)
	})
	public String getEventStreams(@Parameter(hidden = true) @RequestHeader("Accept") String contentType) {
		List<EventStreamResponse> eventStreamResponses = eventStreamService.retrieveAllEventStreams().stream()
				.map(eventStream -> {
					List<ViewSpecification> views = List.of();
					ShaclShape shaclShape = shaclShapeService.retrieveShaclShape(eventStream.getCollection());
					return new EventStreamResponse(eventStream.getCollection(), eventStream.getTimestampPath(),
							eventStream.getVersionOfPath(), views, shaclShape.getModel());
				}).toList();

		return EventStreamHttpConverter.toString(eventStreamResponses, contentType);
	}

	@PutMapping(consumes = { contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle })
	@Operation(summary = "Create an Event Stream based on the provided config")
	public String putEventStream(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "A valid RDF model defining the Event Stream") @RequestBody String configuredEventStream,
			@Parameter(hidden = true) @RequestHeader("Content-Type") String contentType) {
		Model eventStreamModel = ModelConverter.toModel(configuredEventStream, contentType);
		eventStreamValidator.validateShape(eventStreamModel);

		EventStreamResponse eventStreamResponse = eventStreamResponseConverter.fromModel(eventStreamModel);
		EventStream eventStream = new EventStream(
				eventStreamResponse.getCollection(),
				eventStreamResponse.getTimestampPath(),
				eventStreamResponse.getVersionOfPath());
		ShaclShape shaclShape = new ShaclShape(
				eventStreamResponse.getCollection(),
				eventStreamResponse.getShacl());
		eventStreamResponse.getViews().forEach(viewService::addView);
		eventStreamService.saveEventStream(eventStream);
		shaclShapeService.updateShaclShape(shaclShape);

		return EventStreamHttpConverter.toString(eventStreamResponse, contentType);
	}

	@GetMapping("/{collectionName}")
	@Operation(summary = "Retrieve specific Event Stream configuration")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeNQuads),
			@Content(mediaType = contentTypeJSONLD),
			@Content(mediaType = contentTypeTurtle)
	})
	public String getEventStream(@PathVariable String collectionName,
			@Parameter(hidden = true) @RequestHeader("Accept") String contentType) {
		EventStream eventStream = eventStreamService.retrieveEventStream(collectionName);
		List<ViewSpecification> views = List.of();
		ShaclShape shaclShape = shaclShapeService.retrieveShaclShape(collectionName);

		EventStreamResponse response = new EventStreamResponse(eventStream.getCollection(),
				eventStream.getTimestampPath(),
				eventStream.getVersionOfPath(), views, shaclShape.getModel());

		return EventStreamHttpConverter.toString(response, contentType);
	}

	@DeleteMapping("/{collectionName}")
	@Operation(summary = "Delete an Event Stream")
	public ResponseEntity<Void> deleteEventStream(@PathVariable String collectionName) {
		// TODO: delete views by collectionName when this is added to the service
		eventStreamService.deleteEventStream(collectionName);
		shaclShapeService.deleteShaclShape(collectionName);
		return ResponseEntity.ok().build();
	}

}
