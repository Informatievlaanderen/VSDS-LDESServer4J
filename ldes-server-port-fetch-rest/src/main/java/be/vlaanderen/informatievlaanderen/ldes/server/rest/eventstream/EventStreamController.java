package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.*;
import java.util.List;

import static org.springframework.http.HttpHeaders.*;

@RestController
@Tag(name = "Fetch")
public class EventStreamController {

	private final RestConfig restConfig;
	private final CachingStrategy cachingStrategy;
	private final EventStreamService eventStreamService;
	private final ViewService viewService;
	private final ShaclShapeService shaclShapeService;

	public EventStreamController(RestConfig restConfig, CachingStrategy cachingStrategy,
			EventStreamService eventStreamService, ViewService viewService, ShaclShapeService shaclShapeService) {
		this.restConfig = restConfig;
		this.cachingStrategy = cachingStrategy;
		this.eventStreamService = eventStreamService;
		this.viewService = viewService;
		this.shaclShapeService = shaclShapeService;
	}

	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "{collectionname}")
	@Operation(summary = "Retrieve an Linked Data Event Stream")
	public ResponseEntity<EventStreamResponse> retrieveLdesFragment(@RequestHeader(HttpHeaders.ACCEPT) String language,
	public ResponseEntity<EventStream> retrieveLdes(@RequestHeader(HttpHeaders.ACCEPT) String language,
			HttpServletResponse response, @PathVariable("collectionname") String collectionName) {
		EventStream eventStream = eventStreamService.retrieveEventStream(collectionName);
		ShaclShape shape = shaclShapeService.retrieveShaclShape(collectionName);
		List<ViewSpecification> views = viewService.getViewsByCollectionName(collectionName);

		EventStreamResponse eventStreamResponse = new EventStreamResponse(eventStream.getCollection(),
				eventStream.getTimestampPath(), eventStream.getVersionOfPath(), views, shape.getModel());

		response.setHeader(CACHE_CONTROL, restConfig.generateImmutableCacheControl());
		response.setHeader(CONTENT_DISPOSITION, RestConfig.INLINE);
		setContentTypeHeader(language, response);

		return ResponseEntity
				.ok()
				.eTag(cachingStrategy.generateCacheIdentifier(eventStreamResponse))
				.body(eventStreamResponse);
	}

	private void setContentTypeHeader(String language, HttpServletResponse response) {
		if (language.equals(MediaType.ALL_VALUE) || language.contains(MediaType.TEXT_HTML_VALUE))
			response.setHeader(CONTENT_TYPE, RestConfig.TEXT_TURTLE);
		else
			response.setHeader(CONTENT_TYPE, language.split(",")[0]);
	}
}
