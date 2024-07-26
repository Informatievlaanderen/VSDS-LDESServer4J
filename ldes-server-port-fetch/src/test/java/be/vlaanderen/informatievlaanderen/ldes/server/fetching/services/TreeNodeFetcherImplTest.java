package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.TreeNodeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TreeNodeFetcherImplTest {
	private static final String COLLECTION = "collectionName";
	private static final String VIEW = "view";
	private static final ViewName VIEW_NAME = new ViewName(COLLECTION, VIEW);
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private TreeNodeRepository treeNodeRepository;
	private TreeNodeFetcherImpl treeNodeFetcher;

	@BeforeEach
	void setUp() {
		treeNodeRepository = mock(TreeNodeRepository.class);
		treeNodeFetcher = new TreeNodeFetcherImpl(treeNodeRepository);
	}

	@Test
	void when_getFragment_WhenNoFragmentExists_ThenMissingResourceExceptionIsThrown() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(ldesFragmentRequest.viewName(),
				ldesFragmentRequest.fragmentPairs()));
		Mockito.when(treeNodeRepository.findByFragmentIdentifier(fragment.getFragmentId()))
				.thenThrow(new MissingResourceException("fragment", fragment.getFragmentIdString()));

		assertThatThrownBy(() -> treeNodeFetcher.getFragment(ldesFragmentRequest))
				.isInstanceOf(MissingResourceException.class)
				.hasMessage("Resource of type: TreeNode with id: /collectionName/view?generatedAtTime=2020-12-28T09:36:09.72Z could not be found.");
	}

	@Test
	void when_getFragment_WhenExactFragmentExists_ThenReturnThatFragment() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(ldesFragmentRequest.viewName(),
				ldesFragmentRequest.fragmentPairs()));
		TreeNode treeNode = new TreeNode(fragment.getFragmentIdString(), true, false, List.of(),
				List.of(), "collectionName", null);

		when(treeNodeRepository.findByFragmentIdentifier(fragment.getFragmentId())).thenReturn(Optional.of(treeNode));

		TreeNode returnedTreeNode = treeNodeFetcher.getFragment(ldesFragmentRequest);

		assertThat(returnedTreeNode).isEqualTo(treeNode);
	}
}
