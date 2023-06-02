package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.*;

@RestController
public class EventStreamController implements OpenApiEventStreamController {

	private final RestConfig restConfig;
	private final CachingStrategy cachingStrategy;
	private final EventStreamService eventStreamService;

	public EventStreamController(RestConfig restConfig, CachingStrategy cachingStrategy,
			EventStreamService eventStreamService) {
		this.restConfig = restConfig;
		this.cachingStrategy = cachingStrategy;
		this.eventStreamService = eventStreamService;
	}

	@Override
	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "{collectionname}")
	public ResponseEntity<EventStreamResponse> retrieveLdesFragment(
			@RequestHeader(HttpHeaders.ACCEPT) String language,
			HttpServletResponse response, @PathVariable("collectionname") String collectionName) {
		EventStreamResponse eventStream = eventStreamService.retrieveEventStream(collectionName);

		response.setHeader(CACHE_CONTROL, restConfig.generateImmutableCacheControl());
		response.setHeader(CONTENT_DISPOSITION, RestConfig.INLINE);
		setContentTypeHeader(language, response);

		return ResponseEntity
				.ok()
				.eTag(cachingStrategy.generateCacheIdentifier(eventStream.getCollection(), language))
				.body(eventStream);
	}

	private void setContentTypeHeader(String language, HttpServletResponse response) {
		if (language.equals(MediaType.ALL_VALUE) || language.contains(MediaType.TEXT_HTML_VALUE))
			response.setHeader(CONTENT_TYPE, RestConfig.TEXT_TURTLE);
		else
			response.setHeader(CONTENT_TYPE, language.split(",")[0]);
	}
}
