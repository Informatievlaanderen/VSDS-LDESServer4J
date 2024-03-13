package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.TreeNodeFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import io.micrometer.observation.annotation.Observed;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.config.TreeViewWebConfig.DEFAULT_RDF_MEDIA_TYPE;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Observed
@RestController
public class TreeNodeController implements OpenApiTreeNodeController {

	private final RestConfig restConfig;
	private final TreeNodeFetcher treeNodeFetcher;
	private final CachingStrategy cachingStrategy;

	public TreeNodeController(RestConfig restConfig, TreeNodeFetcher treeNodeFetcher, CachingStrategy cachingStrategy) {
		this.restConfig = restConfig;
		this.treeNodeFetcher = treeNodeFetcher;
		this.cachingStrategy = cachingStrategy;
	}

	@Override
	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "{collectionname}/{view}")
	public ResponseEntity<TreeNode> retrieveLdesFragment(@PathVariable("view") String view,
														 @RequestParam Map<String, String> requestParameters,
														 @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = DEFAULT_RDF_MEDIA_TYPE) String language,
														 @PathVariable("collectionname") String collectionName) {
		final ViewName viewName = new ViewName(collectionName, view);
		TreeNode treeNode = getFragment(viewName, requestParameters);
		return ResponseEntity
				.ok()
				.header(CONTENT_TYPE, getContentTypeHeader(language))
				.header(HttpHeaders.CONTENT_DISPOSITION, RestConfig.INLINE)
				.header(CACHE_CONTROL, getCacheControlHeader(treeNode))
				.eTag(cachingStrategy.generateCacheIdentifier(treeNode, language))
				.body(treeNode);
	}

	private TreeNode getFragment(ViewName viewName, Map<String, String> fragmentationMap) {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(viewName,
				fragmentationMap.entrySet()
						.stream()
						.map(entry -> new FragmentPair(entry.getKey(), entry.getValue()))
						.toList());

		return treeNodeFetcher.getFragment(ldesFragmentRequest);
	}

	private String getCacheControlHeader(TreeNode treeNode) {
		return treeNode.isImmutable()
				? restConfig.generateImmutableCacheControl()
				: restConfig.generateMutableCacheControl(treeNode.getNextUpdateTs());
	}

	private String getContentTypeHeader(String language) {
		if (language.equals(MediaType.ALL_VALUE) || language.contains(MediaType.TEXT_HTML_VALUE))
			return RestConfig.TEXT_TURTLE;
		else
			return language.split(",")[0];
	}

}
