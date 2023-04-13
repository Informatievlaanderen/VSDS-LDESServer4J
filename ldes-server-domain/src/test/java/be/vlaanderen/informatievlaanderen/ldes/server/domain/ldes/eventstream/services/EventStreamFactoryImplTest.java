package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services.TreeNodeFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventStreamFactoryImplTest {
	EventStreamFactory eventStreamFactory;

	private TreeNodeFetcher treeNodeFetcher;

	@BeforeEach
	void setUp() {
		treeNodeFetcher = mock(TreeNodeFetcher.class);
		eventStreamFactory = new EventStreamFactoryImpl(treeNodeFetcher);
	}

	@Test
	void test() {
		when(treeNodeFetcher.getFragment(any()))
				.thenReturn(createView("firstView"))
				.thenReturn(createView("secondView"));

		EventStream eventStream = eventStreamFactory.getEventStream(getLdesSpecification());
		assertEquals("mobility-hindrances", eventStream.collection());
		assertEquals("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape",
				eventStream.shape());
		assertEquals("http://www.w3.org/ns/prov#generatedAtTime", eventStream.timestampPath());
		assertEquals("http://purl.org/dc/terms/isVersionOf", eventStream.versionOfPath());
		List<String> viewsInEventStream = eventStream.views().stream().map(TreeNode::getFragmentId).toList();
		assertTrue(viewsInEventStream.contains("/firstView"));
		assertTrue(viewsInEventStream.contains("/secondView"));
	}

	private LdesSpecification getLdesSpecification() {
		ViewSpecification firstViewSpecification = new ViewSpecification();
		firstViewSpecification.setName("firstView");
		ViewSpecification secondViewSpecification = new ViewSpecification();
		secondViewSpecification.setName("secondView");
		LdesSpecification ldesSpecification = new LdesSpecification();
		ldesSpecification.setCollectionName("mobility-hindrances");
		ldesSpecification.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
		ldesSpecification.setVersionOf("http://purl.org/dc/terms/isVersionOf");
		ldesSpecification.validation()
				.setShape("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape");
		ldesSpecification.setViews(List.of(firstViewSpecification, secondViewSpecification));
		return ldesSpecification;
	}

	private TreeNode createView(String viewName) {
		return new TreeNode("/" + viewName, false, false, true, List.of(), List.of(), "collectionName");
	}
}