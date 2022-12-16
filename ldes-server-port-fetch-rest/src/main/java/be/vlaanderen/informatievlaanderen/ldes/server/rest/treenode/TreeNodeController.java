package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services.TreeNodeFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;

@RestController
public class TreeNodeController {

	private final RestConfig restConfig;
	private final TreeNodeFetcher treeNodeFetcher;
	private final CachingStrategy cachingStrategy;

	public TreeNodeController(RestConfig restConfig, TreeNodeFetcher treeNodeFetcher, CachingStrategy cachingStrategy) {
		this.restConfig = restConfig;
		this.treeNodeFetcher = treeNodeFetcher;
		this.cachingStrategy = cachingStrategy;
	}

	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "${ldes.collectionname}/{view}")
	public ResponseEntity<TreeNode> retrieveLdesFragment(HttpServletResponse response,
			@PathVariable("view") String viewName,
			@RequestParam Map<String, String> requestParameters, @RequestHeader(HttpHeaders.ACCEPT) String language) {
		TreeNode treeNode = returnRequestedTreeNode(response, viewName, requestParameters);
		setContentTypeHeader(language, response);
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, RestConfig.INLINE);
		return ResponseEntity
				.ok()
				.eTag(cachingStrategy.generateCacheIdentifier(treeNode))
				.body(treeNode);
	}

	private TreeNode returnRequestedTreeNode(HttpServletResponse response, String viewName,
			Map<String, String> fragmentationMap) {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(viewName,
				fragmentationMap.entrySet()
						.stream().map(entry -> new FragmentPair(entry.getKey(), entry.getValue())).toList());

		TreeNode treeNode = treeNodeFetcher.getFragment(ldesFragmentRequest);
		setCacheControlHeader(response, treeNode);
		setEtagHeader(response, treeNode);
		return treeNode;

	}

	private void setCacheControlHeader(HttpServletResponse response, TreeNode treeNode) {
		if (treeNode.isImmutable()) {
			response.setHeader(CACHE_CONTROL, restConfig.generateImmutableCacheControl());
		} else {
			response.setHeader(CACHE_CONTROL, restConfig.generateMutableCacheControl());
		}
	}

	private void setContentTypeHeader(String language, HttpServletResponse response) {
		if (language.equals(MediaType.ALL_VALUE) || language.contains(MediaType.TEXT_HTML_VALUE))
			response.setHeader(CONTENT_TYPE, RestConfig.TEXT_TURTLE);
		else
			response.setHeader(CONTENT_TYPE, language.split(",")[0]);
	}

	private void setEtagHeader(HttpServletResponse response, TreeNode treeNode) {
		response.setHeader(HttpHeaders.ETAG, cachingStrategy.generateCacheIdentifier(treeNode));
	}
}
