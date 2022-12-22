package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${ldes.collectionname}")
	private String collectionName;

	private final RestConfig restConfig;
	private final EventStreamFetcher eventStreamFetcher;
	private final CachingStrategy cachingStrategy;

	public EventStreamController(RestConfig restConfig, EventStreamFetcher eventStreamFetcher,
			CachingStrategy cachingStrategy) {
		this.restConfig = restConfig;
		this.eventStreamFetcher = eventStreamFetcher;
		this.cachingStrategy = cachingStrategy;
	}

	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "${ldes.collectionname}")
	public ResponseEntity<EventStream> retrieveLdesFragment(@RequestHeader(HttpHeaders.ACCEPT) String language,
			HttpServletResponse response) {
		EventStream eventStream = eventStreamFetcher.fetchEventStream();

		response.setHeader(CACHE_CONTROL, restConfig.generateImmutableCacheControl());
		response.setHeader(CONTENT_DISPOSITION, RestConfig.INLINE);
		setContentTypeHeader(language, response);

		return ResponseEntity
				.ok()
				.eTag(cachingStrategy.generateCacheIdentifier(eventStream))
				.body(eventStream);
	}

	private void setContentTypeHeader(String language, HttpServletResponse response) {
		if (language.equals(MediaType.ALL_VALUE) || language.contains(MediaType.TEXT_HTML_VALUE))
			response.setHeader(CONTENT_TYPE, RestConfig.TEXT_TURTLE);
		else
			response.setHeader(CONTENT_TYPE, language.split(",")[0]);
	}
}
