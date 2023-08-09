package fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.TreeNodeFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.TreeNodeFetcherImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;

class TreeNodeFetcherImplTest {
	private static final String COLLECTION = "collectionName";
	private static final String VIEW = "view";
	private static final ViewName VIEW_NAME = new ViewName(COLLECTION, VIEW);
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private TreeNodeFactory treeNodeFactory;
	private TreeNodeFetcherImpl treeNodeFetcher;
	private String hostName = "http://localhost:8089";

	@BeforeEach
	void setUp() {
		treeNodeFactory = Mockito.mock(TreeNodeFactory.class);
		treeNodeFetcher = new TreeNodeFetcherImpl(hostName, treeNodeFactory);
	}

	@Test
	void when_getFragment_WhenNoFragmentExists_ThenMissingFragmentExceptionIsThrown() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(ldesFragmentRequest.viewName(),
				ldesFragmentRequest.fragmentPairs()));
		Mockito.when(treeNodeFactory.getTreeNode(fragment.getFragmentId(), hostName,
				COLLECTION))
				.thenThrow(new MissingFragmentException(fragment.getFragmentIdString()));

		MissingFragmentException missingFragmentException = Assertions.assertThrows(MissingFragmentException.class,
				() -> treeNodeFetcher.getFragment(ldesFragmentRequest));

		Assertions.assertEquals(
				"No fragment exists with fragment identifier: /collectionName/view?generatedAtTime=2020-12-28T09:36:09.72Z",
				missingFragmentException.getMessage());
	}

	@Test
	void when_getFragment_WhenExactFragmentExists_ThenReturnThatFragment() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(ldesFragmentRequest.viewName(),
				ldesFragmentRequest.fragmentPairs()));
		TreeNode treeNode = new TreeNode(fragment.getFragmentIdString(), true, false, List.of(),
				List.of(), "collectionName");
		Mockito.when(treeNodeFactory.getTreeNode(fragment.getFragmentId(), hostName,
				COLLECTION))
				.thenReturn(treeNode);

		TreeNode returnedTreeNode = treeNodeFetcher.getFragment(ldesFragmentRequest);

		Assertions.assertEquals(treeNode, returnedTreeNode);
	}
}
