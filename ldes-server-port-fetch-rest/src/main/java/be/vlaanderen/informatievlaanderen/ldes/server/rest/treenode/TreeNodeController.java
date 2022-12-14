package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentFetchService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class TreeNodeController {

	private static final String CACHE_CONTROL_HEADER = "Cache-Control";
	private static final String CACHE_CONTROL_IMMUTABLE = "public, max-age=604800, immutable";
	private static final String CACHE_CONTROL_MUTABLE = "public, max-age=60";
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String TEXT_TURTLE = "text/turtle";
	private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
	private static final String INLINE = "inline";

	private final FragmentFetchService fragmentFetchService;

	public TreeNodeController(FragmentFetchService fragmentFetchService) {
		this.fragmentFetchService = fragmentFetchService;
	}

	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "/{view}")
	public LdesFragment retrieveLdesFragment(HttpServletResponse response,
			@PathVariable("view") String viewName,
			@RequestParam Map<String, String> requestParameters, @RequestHeader(HttpHeaders.ACCEPT) String language) {
		setContentTypeHeader(language, response);
		response.setHeader(CONTENT_DISPOSITION_HEADER, INLINE);
		return returnRequestedFragment(response, viewName, requestParameters);
	}

	private LdesFragment returnRequestedFragment(HttpServletResponse response, String viewName,
			Map<String, String> fragmentationMap) {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(viewName,
				fragmentationMap.entrySet()
						.stream().map(entry -> new FragmentPair(entry.getKey(), entry.getValue())).toList());

		LdesFragment fragment = fragmentFetchService.getFragment(ldesFragmentRequest);
		setCacheControlHeader(response, fragment);
		return fragment;

	}

	private void setCacheControlHeader(HttpServletResponse response, LdesFragment fragment) {
		if (fragment.isImmutable()) {
			response.setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_IMMUTABLE);
		} else {
			response.setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_MUTABLE);
		}
	}

	private void setContentTypeHeader(String language, HttpServletResponse response) {
		if (language.equals(MediaType.ALL_VALUE) || language.contains(MediaType.TEXT_HTML_VALUE))
			response.setHeader(CONTENT_TYPE_HEADER, TEXT_TURTLE);
		else
			response.setHeader(CONTENT_TYPE_HEADER, language.split(",")[0]);
	}

}
