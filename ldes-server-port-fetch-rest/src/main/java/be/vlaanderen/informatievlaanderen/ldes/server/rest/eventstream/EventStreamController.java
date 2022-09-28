package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class EventStreamController {
	private static final String CACHE_CONTROL_HEADER = "Cache-Control";
	private static final String CACHE_CONTROL_IMMUTABLE = "public, max-age=604800, immutable";

	@Value("${ldes.collectionname}")
	private String collectionName;

	private final EventStreamFetcher eventStreamFetcher;

	public EventStreamController(EventStreamFetcher eventStreamFetcher) {
		this.eventStreamFetcher = eventStreamFetcher;
	}

	@GetMapping(value = "${ldes.collectionname}", produces = { "application/turtle", "application/ld+json",
			"application/n-quads" })
	EventStream retrieveLdesFragment(HttpServletResponse response) {
		response.setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_IMMUTABLE);
		return eventStreamFetcher.fetchEventStream();
	}
}
