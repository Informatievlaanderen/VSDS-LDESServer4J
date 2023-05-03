package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DeletedFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TreeNodeFetcherImplTest {
	private static final String VIEW = "view";
	private static final ViewName VIEW_NAME = new ViewName("collectionName", VIEW);
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private TreeNodeFactory treeNodeFactory;
	private TreeNodeFetcherImpl treeNodeFetcher;
	private LdesConfig ldesConfig;

	@BeforeEach
	void setUp() {
		treeNodeFactory = mock(TreeNodeFactory.class);
		AppConfig appConfig = new AppConfig();
		ldesConfig = new LdesConfig();
		ldesConfig.setCollectionName("collectionName");
		appConfig.setCollections(List.of(ldesConfig));
		ldesConfig.setHostName("http://localhost:8089");
		treeNodeFetcher = new TreeNodeFetcherImpl(appConfig, treeNodeFactory);
	}

	@Test
	void when_getFragment_WhenNoFragmentExists_ThenMissingFragmentExceptionIsThrown() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		LdesFragment ldesFragment = new LdesFragment(ldesFragmentRequest.viewName(),
				ldesFragmentRequest.fragmentPairs());
		when(treeNodeFactory.getTreeNode(ldesFragment.getFragmentId(), ldesConfig.getHostName(), ldesConfig.getCollectionName()))
				.thenThrow(new MissingFragmentException(ldesFragment.getFragmentId()));

		MissingFragmentException missingFragmentException = assertThrows(MissingFragmentException.class,
				() -> treeNodeFetcher.getFragment(ldesFragmentRequest));

		assertEquals(
				"No fragment exists with fragment identifier: /collectionName/view?generatedAtTime=2020-12-28T09:36:09.72Z",
				missingFragmentException.getMessage());
	}

	@Test
	void when_getFragment_WhenFragmentIsDeleted_ThenDeletedFragmentExceptionIsThrown() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		LdesFragment ldesFragment = new LdesFragment(ldesFragmentRequest.viewName(),
				ldesFragmentRequest.fragmentPairs());
		TreeNode treeNode = new TreeNode(ldesFragment.getFragmentId(), true, true, false, List.of(),
				List.of(), "collectionName");
		when(treeNodeFactory.getTreeNode(ldesFragment.getFragmentId(), ldesConfig.getHostName(), ldesConfig.getCollectionName()))
				.thenReturn(treeNode);

		DeletedFragmentException deletedFragmentException = assertThrows(DeletedFragmentException.class,
				() -> treeNodeFetcher.getFragment(ldesFragmentRequest));
		assertEquals(
				"Fragment with following identifier has been deleted: http://localhost:8089/collectionName/view?generatedAtTime=2020-12-28T09:36:09.72Z",
				deletedFragmentException.getMessage());
	}

	@Test
	void when_getFragment_WhenExactFragmentExists_ThenReturnThatFragment() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		LdesFragment ldesFragment = new LdesFragment(ldesFragmentRequest.viewName(),
				ldesFragmentRequest.fragmentPairs());
		TreeNode treeNode = new TreeNode(ldesFragment.getFragmentId(), true, false, false, List.of(),
				List.of(), "collectionName");
		when(treeNodeFactory.getTreeNode(ldesFragment.getFragmentId(), ldesConfig.getHostName(), ldesConfig.getCollectionName()))
				.thenReturn(treeNode);

		TreeNode returnedTreeNode = treeNodeFetcher.getFragment(ldesFragmentRequest);

		assertEquals(treeNode, returnedTreeNode);
	}
}