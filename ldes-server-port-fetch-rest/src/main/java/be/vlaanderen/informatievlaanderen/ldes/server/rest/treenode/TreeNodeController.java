package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.PrefixConstructor;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.StreamingTreeNodeFactoryImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.TreeNodeFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.exceptionhandling.exceptions.ConnectionException;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services.TreeNodeStreamConverterImpl;
import io.micrometer.observation.annotation.Observed;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.config.TreeViewWebConfig.DEFAULT_RDF_MEDIA_TYPE;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Observed
@RestController
public class TreeNodeController/* implements OpenApiTreeNodeController */{
	private final RestConfig restConfig;
	private final TreeNodeFetcher treeNodeFetcher;
	private final StreamingTreeNodeFactoryImpl streamingTreeNodeFactory;
	private final CachingStrategy cachingStrategy;
	private static final Logger log = LoggerFactory.getLogger(TreeNodeController.class);

	public TreeNodeController(RestConfig restConfig, TreeNodeFetcher treeNodeFetcher, StreamingTreeNodeFactoryImpl streamingTreeNodeFactory, CachingStrategy cachingStrategy) {
		this.restConfig = restConfig;
		this.treeNodeFetcher = treeNodeFetcher;
        this.streamingTreeNodeFactory = streamingTreeNodeFactory;
        this.cachingStrategy = cachingStrategy;
	}

//	@Override
	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "{collectionname}/{view}", produces = { MediaType.TEXT_EVENT_STREAM_VALUE })
	public ResponseEntity<ResponseBodyEmitter> retrieveLdesFragmentStreaming(@PathVariable("view") String view,
														 @RequestParam Map<String, String> requestParameters,
														 @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = DEFAULT_RDF_MEDIA_TYPE) String language,
														 @PathVariable("collectionname") String collectionName) {
		final ViewName viewName = new ViewName(collectionName, view);

		ExecutorService executor
				= Executors.newCachedThreadPool();
		SseEmitter emitter = new SseEmitter(100000000000L);

		TreeNode treeNode = getFragmentWithoutMembers(viewName, requestParameters);

		TreeNodeStreamConverterImpl converter = new TreeNodeStreamConverterImpl(collectionName,
				new PrefixConstructor("", false), treeNode.getFragmentId());
//		String contentTypeString = getContentTypeHeader(language);
		MediaType contentType = MediaType.parseMediaType(Lang.RDFPROTO.getHeaderString());

		executor.submit(() -> {
			try {
				emitter.send(converter.getMetaDataStatements(treeNode), contentType);
				streamingTreeNodeFactory.getMembersOfFragment(treeNode.getFragmentId()).map(converter::getMemberStatements).forEach(model -> {
					try {
						emitter.send(model, contentType);

					} catch (IOException exception) {
						log.error("Error while sending LDES member: {}", exception.getMessage());
						emitter.completeWithError(exception);
					}
				});

				emitter.complete();
			} catch (Exception exception) {
				String message = String.format("Error while sending LDES fragment: %s", exception.getMessage());
				log.error(message);
                try {
                    emitter.send(SseEmitter.event().data(message).name("error"));
                } catch (IOException e) {
                    throw new ConnectionException("Could not send previous error message to client", e);
                }
                emitter.completeWithError(exception);
			}

		});


		return ResponseEntity
				.ok()
				.header(CONTENT_TYPE, MediaType.TEXT_EVENT_STREAM_VALUE)
				.header(HttpHeaders.CONTENT_DISPOSITION, RestConfig.INLINE)
				.header(CACHE_CONTROL, getCacheControlHeader(treeNode))
//				.eTag(cachingStrategy.generateCacheIdentifier(treeNode, language))
				.body(emitter);
	}

	private TreeNode getFragment(ViewName viewName, Map<String, String> fragmentationMap) {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(viewName,
				fragmentationMap.entrySet()
						.stream()
						.map(entry -> new FragmentPair(entry.getKey(), entry.getValue()))
						.toList());

		return treeNodeFetcher.getFragment(ldesFragmentRequest);
	}

	private TreeNode getFragmentWithoutMembers(ViewName viewName, Map<String, String> fragmentationMap) {
		List<FragmentPair> fragmentPairs = fragmentationMap.entrySet()
				.stream()
				.map(entry -> new FragmentPair(entry.getKey(), entry.getValue()))
				.toList();
		LdesFragmentIdentifier treeNodeId = new LdesFragmentIdentifier(viewName, fragmentPairs);

		return streamingTreeNodeFactory.getFragmentWithoutMemberData(treeNodeId);
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
