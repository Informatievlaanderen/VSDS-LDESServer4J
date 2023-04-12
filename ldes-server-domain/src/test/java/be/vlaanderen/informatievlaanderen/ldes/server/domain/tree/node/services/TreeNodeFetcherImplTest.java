package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfigDeprecated;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DeletedFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TreeNodeFetcherImplTest {
	private static final String VIEW_NAME = "view";
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private TreeNodeFactory treeNodeFactory;
	private TreeNodeFetcherImpl treeNodeFetcher;

	@BeforeEach
	void setUp() {
		treeNodeFactory = mock(TreeNodeFactory.class);
		LdesConfigDeprecated ldesConfig = new LdesConfigDeprecated();
		ldesConfig.setHostName("http://localhost:8089");
		treeNodeFetcher = new TreeNodeFetcherImpl(ldesConfig,
				treeNodeFactory);
	}

	@Test
	void when_getFragment_WhenNoFragmentExists_ThenMissingFragmentExceptionIsThrown() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		when(treeNodeFactory.getTreeNode(ldesFragmentRequest.generateFragmentId()))
				.thenThrow(new MissingFragmentException(ldesFragmentRequest.generateFragmentId()));

		MissingFragmentException missingFragmentException = assertThrows(MissingFragmentException.class,
				() -> treeNodeFetcher.getFragment(ldesFragmentRequest));

		assertEquals(
				"No fragment exists with fragment identifier: /view?generatedAtTime=2020-12-28T09:36:09.72Z",
				missingFragmentException.getMessage());
	}

	@Test
	void when_getFragment_WhenFragmentIsDeleted_ThenDeletedFragmentExceptionIsThrown() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		TreeNode treeNode = new TreeNode(ldesFragmentRequest.generateFragmentId(), true, true, false, List.of(),
				List.of());
		when(treeNodeFactory.getTreeNode(ldesFragmentRequest.generateFragmentId()))
				.thenReturn(treeNode);

		DeletedFragmentException deletedFragmentException = assertThrows(DeletedFragmentException.class,
				() -> treeNodeFetcher.getFragment(ldesFragmentRequest));
		assertEquals(
				"Fragment with following identifier has been deleted: http://localhost:8089/view?generatedAtTime=2020-12-28T09:36:09.72Z",
				deletedFragmentException.getMessage());
	}

	@Test
	void when_getFragment_WhenExactFragmentExists_ThenReturnThatFragment() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		TreeNode treeNode = new TreeNode(ldesFragmentRequest.generateFragmentId(), true, false, false, List.of(),
				List.of());
		when(treeNodeFactory.getTreeNode(ldesFragmentRequest.generateFragmentId()))
				.thenReturn(treeNode);

		TreeNode returnedTreeNode = treeNodeFetcher.getFragment(ldesFragmentRequest);

		assertEquals(treeNode, returnedTreeNode);
	}
}