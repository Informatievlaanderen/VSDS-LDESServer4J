package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.CollectionNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.EventStreamFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.*;

@RestController
public class EventStreamController {

	private final RestConfig restConfig;
	private final EventStreamFactory eventStreamFactory;
	private final CachingStrategy cachingStrategy;
	private final AppConfig appConfig;

	public EventStreamController(RestConfig restConfig, EventStreamFactory eventStreamFactory,
			CachingStrategy cachingStrategy, AppConfig appConfig) {
		this.restConfig = restConfig;
		this.eventStreamFactory = eventStreamFactory;
		this.cachingStrategy = cachingStrategy;
		this.appConfig = appConfig;
	}

	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "{collectionname}")
	public ResponseEntity<EventStream> retrieveLdesFragment(@RequestHeader(HttpHeaders.ACCEPT) String language,
			HttpServletResponse response, @PathVariable("collectionname") String collectionName) {

		LdesConfig ldesConfig = appConfig.getLdesConfig(collectionName)
				.orElseThrow(() -> new CollectionNotFoundException(collectionName));
		EventStream eventStream = eventStreamFactory.getEventStream(ldesConfig);

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
