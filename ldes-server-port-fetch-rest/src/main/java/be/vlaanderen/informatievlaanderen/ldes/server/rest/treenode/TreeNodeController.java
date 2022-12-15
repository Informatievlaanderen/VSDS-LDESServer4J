package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentFetchService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

	private final TreeNodeFetcher treeNodeFetcher;
	private final CachingStrategy cachingStrategy;

	public TreeNodeController(TreeNodeFetcher treeNodeFetcher) {
		this.treeNodeFetcher = treeNodeFetcher;
		this.cachingStrategy = cachingStrategy;
	}

	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "/{view}")
	public TreeNode retrieveLdesFragment(HttpServletResponse response,
			@PathVariable("view") String viewName,
			@RequestParam Map<String, String> requestParameters, @RequestHeader(HttpHeaders.ACCEPT) String language) {
		setContentTypeHeader(language, response);
		response.setHeader(CONTENT_DISPOSITION_HEADER, INLINE);
		return returnRequestedTreeNode(response, viewName, requestParameters);
	}

	private TreeNode returnRequestedTreeNode(HttpServletResponse response, String viewName,
			Map<String, String> fragmentationMap) {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(viewName,
				fragmentationMap.entrySet()
						.stream().map(entry -> new FragmentPair(entry.getKey(), entry.getValue())).toList());

		TreeNode treeNode = treeNodeFetcher.getFragment(ldesFragmentRequest);
		setCacheControlHeader(response, treeNode);
		setEtagHeader(response, fragment);
		return treeNode;

	}

	private void setCacheControlHeader(HttpServletResponse response, TreeNode treeNode) {
		if (treeNode.isImmutable()) {
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

	private void setEtagHeader(HttpServletResponse response, LdesFragment ldesFragment) {
		response.setHeader(HttpHeaders.ETAG, cachingStrategy.generateCacheIdentifier(ldesFragment));
	}
}
