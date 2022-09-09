package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentFetchService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class LdesFragmentController {

	private static final String CACHE_CONTROL_HEADER = "Cache-Control";
	private static final String CACHE_CONTROL_IMMUTABLE = "public, max-age=604800, immutable";
	private static final String CACHE_CONTROL_MUTABLE = "public, max-age=60";

	private final FragmentFetchService fragmentFetchService;

	public LdesFragmentController(FragmentFetchService fragmentFetchService) {
		this.fragmentFetchService = fragmentFetchService;
	}

	@GetMapping(value = "/{view}", produces = { "application/turtle", "application/ld+json",
			"application/n-quads" })
	LdesFragment retrieveLdesFragment(HttpServletResponse response,
			@PathVariable("view") String viewName,
			@RequestParam Map<String, String> requestParameters) {
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

}
