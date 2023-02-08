package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.EventStreamFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.*;

@RestController
public class EventStreamController {

	private final RestConfig restConfig;
	private final EventStreamFactory eventStreamFactory;
	private final CachingStrategy cachingStrategy;

	public EventStreamController(RestConfig restConfig, EventStreamFactory eventStreamFactory,
			CachingStrategy cachingStrategy) {
		this.restConfig = restConfig;
		this.eventStreamFactory = eventStreamFactory;
		this.cachingStrategy = cachingStrategy;
	}

	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "${ldes.collectionname}")
	public ResponseEntity<EventStreamResponse> retrieveLdesFragment(@RequestHeader(HttpHeaders.ACCEPT) String language,
			HttpServletResponse response) {
		EventStreamResponse eventStreamResponse = eventStreamFactory.getEventStream();

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
