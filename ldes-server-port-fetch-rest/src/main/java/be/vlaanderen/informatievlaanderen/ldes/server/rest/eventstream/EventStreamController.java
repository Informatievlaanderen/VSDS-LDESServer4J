package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.apache.jena.riot.WebContent.*;
import static org.springframework.http.HttpHeaders.*;

@RestController
@Tag(name = "Fetch")
public class EventStreamController {

	private final RestConfig restConfig;
	private final CachingStrategy cachingStrategy;
	private final EventStreamService eventStreamService;

	public EventStreamController(RestConfig restConfig, CachingStrategy cachingStrategy,
			EventStreamService eventStreamService) {
		this.restConfig = restConfig;
		this.cachingStrategy = cachingStrategy;
		this.eventStreamService = eventStreamService;
	}

	@GetMapping("/")
	public Model getDcat(@RequestHeader(HttpHeaders.ACCEPT) String language, HttpServletResponse response) {
		setContentTypeHeader(language, response);
		return eventStreamService.getComposedDcat();
	}

	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "{collectionname}")
	@Operation(summary = "Retrieve an Linked Data Event Stream")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = contentTypeNQuads),
			@Content(mediaType = contentTypeJSONLD),
			@Content(mediaType = contentTypeTurtle)
	})
	public ResponseEntity<EventStreamResponse> retrieveLdesFragment(
			@Parameter(hidden = true) @RequestHeader(HttpHeaders.ACCEPT) String language,
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
		if (language.equals(MediaType.ALL_VALUE) || language.contains(MediaType.TEXT_HTML_VALUE)) {
			response.setHeader(CONTENT_TYPE, RestConfig.TEXT_TURTLE);
		} else {
			response.setHeader(CONTENT_TYPE, language.split(",")[0]);
		}
	}
}
