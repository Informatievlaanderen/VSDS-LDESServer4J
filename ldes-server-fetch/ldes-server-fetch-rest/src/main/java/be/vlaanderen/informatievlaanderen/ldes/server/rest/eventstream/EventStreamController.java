package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamServiceSpi;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.config.EventStreamWebConfig.DEFAULT_RDF_MEDIA_TYPE;
import static org.springframework.http.HttpHeaders.*;

@Observed
@RestController
public class EventStreamController implements OpenApiEventStreamController {

	private final RestConfig restConfig;
	private final CachingStrategy cachingStrategy;
	private final EventStreamServiceSpi eventStreamService;

	public EventStreamController(RestConfig restConfig, CachingStrategy cachingStrategy,
								 EventStreamServiceSpi eventStreamService) {
		this.restConfig = restConfig;
		this.cachingStrategy = cachingStrategy;
		this.eventStreamService = eventStreamService;
	}

	@GetMapping("/")
	public Model getDcat(@RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = DEFAULT_RDF_MEDIA_TYPE) String language, HttpServletResponse response) {
		response.setContentType(getContentTypeHeader(language));
		return eventStreamService.getComposedDcat();
	}

	@Override
	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "{collectionName}")
	public ResponseEntity<EventStreamTO> retrieveLdesFragment(
			@RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = DEFAULT_RDF_MEDIA_TYPE) String language,
			@PathVariable String collectionName) {
		EventStreamTO eventStream = eventStreamService.retrieveEventStream(collectionName);

		return ResponseEntity
				.ok()
				.header(CONTENT_TYPE, getContentTypeHeader(language))
				.header(CACHE_CONTROL, restConfig.generateMutableCacheControl(null))
				.header(CONTENT_DISPOSITION, RestConfig.INLINE)
                .header(VARY, ACCEPT)
				.eTag(cachingStrategy.generateCacheIdentifier(eventStream.getCollection(), language))
				.body(eventStream);
	}

	private String getContentTypeHeader(String language) {
		if (language.equals(MediaType.ALL_VALUE) || language.contains(MediaType.TEXT_HTML_VALUE)) {
			return RestConfig.TEXT_TURTLE;
		} else {
			return language.split(",")[0];
		}
	}
}
