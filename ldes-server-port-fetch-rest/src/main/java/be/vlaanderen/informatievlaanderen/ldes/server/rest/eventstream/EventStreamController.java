package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class EventStreamController {
	private static final String CACHE_CONTROL_HEADER = "Cache-Control";
	private static final String CACHE_CONTROL_IMMUTABLE = "public, max-age=604800, immutable";
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String TEXT_TURTLE = "text/turtle";
	private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
	private static final String INLINE = "inline";

	@Value("${ldes.collectionname}")
	private String collectionName;

	private final EventStreamFetcher eventStreamFetcher;
	private final CachingStrategy cachingStrategy;

	public EventStreamController(EventStreamFetcher eventStreamFetcher, CachingStrategy cachingStrategy) {
		this.eventStreamFetcher = eventStreamFetcher;
		this.cachingStrategy = cachingStrategy;
	}

	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "${ldes.collectionname}")
	public ResponseEntity<EventStream> retrieveLdesFragment(@RequestHeader(HttpHeaders.ACCEPT) String language,
			HttpServletResponse response) {
		EventStream eventStream = eventStreamFetcher.fetchEventStream();

		response.setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_IMMUTABLE);
		response.setHeader(CONTENT_DISPOSITION_HEADER, INLINE);
		setContentTypeHeader(language, response);

		return ResponseEntity
				.ok()
				.eTag(cachingStrategy.generateCacheIdentifier(eventStream))
				.body(eventStream);
	}

	private void setContentTypeHeader(String language, HttpServletResponse response) {
		if (language.equals(MediaType.ALL_VALUE) || language.contains(MediaType.TEXT_HTML_VALUE))
			response.setHeader(CONTENT_TYPE_HEADER, TEXT_TURTLE);
		else
			response.setHeader(CONTENT_TYPE_HEADER, language.split(",")[0]);
	}
}
