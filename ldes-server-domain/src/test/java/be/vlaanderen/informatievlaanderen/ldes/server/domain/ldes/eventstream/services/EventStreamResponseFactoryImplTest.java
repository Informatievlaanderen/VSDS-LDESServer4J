package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.EventStreamInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeNodeInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services.TreeNodeFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventStreamResponseFactoryImplTest {
	EventStreamFactory eventStreamFactory;

	private TreeNodeFetcher treeNodeFetcher;

	@BeforeEach
	void setUp() {
		LdesConfig ldesConfig = getLdesConfig();
		ViewConfig viewConfig = getViewConfig();
		treeNodeFetcher = mock(TreeNodeFetcher.class);
		eventStreamFactory = new EventStreamFactoryImpl(ldesConfig, viewConfig, treeNodeFetcher);
	}

	@Test
	void test() {
		when(treeNodeFetcher.getFragment(LdesFragmentRequest.createViewRequest("view1")))
				.thenReturn(createView("view1"));
		when(treeNodeFetcher.getFragment(LdesFragmentRequest.createViewRequest("view2")))
				.thenReturn(createView("view2"));

		EventStreamResponse eventStreamResponse = eventStreamFactory.getEventStream();

		EventStreamInfoResponse expectedEventStreamInfoResponse = new EventStreamInfoResponse("http://localhost:8080/mobility-hindrances", "http://www.w3.org/ns/prov#generatedAtTime", "http://purl.org/dc/terms/isVersionOf",
                "https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape", List.of("http://localhost:8080/mobility-hindrances/view1", "http://localhost:8080/mobility-hindrances/view2"));
		List<TreeNodeInfoResponse> treeNodeInfoResponses = List.of(new TreeNodeInfoResponse("http://localhost:8080/mobility-hindrances/view1", List.of()), new TreeNodeInfoResponse("http://localhost:8080/mobility-hindrances/view2", List.of()));
		assertEquals(expectedEventStreamInfoResponse, eventStreamResponse.eventStreamInfoResponse());
		assertEquals(treeNodeInfoResponses, eventStreamResponse.views());
	}

	private ViewConfig getViewConfig() {
		ViewConfig viewConfig = new ViewConfig();
		ViewSpecification firstViewSpecification = new ViewSpecification();
		firstViewSpecification.setName("view1");
		ViewSpecification secondViewSpecification = new ViewSpecification();
		secondViewSpecification.setName("view2");
		viewConfig.setViews(List.of(firstViewSpecification, secondViewSpecification));
		return viewConfig;
	}

	private LdesConfig getLdesConfig() {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setHostName("http://localhost:8080");
		ldesConfig.setCollectionName("mobility-hindrances");
		ldesConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
		ldesConfig.setVersionOf("http://purl.org/dc/terms/isVersionOf");
		ldesConfig.validation()
				.setShape("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape");
		return ldesConfig;
	}

	private TreeNode createView(String viewName) {
		return new TreeNode("/" + viewName, false, false, true, List.of(), List.of());
	}
}