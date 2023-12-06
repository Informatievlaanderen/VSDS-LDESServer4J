package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.PrefixConstructor;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import org.springframework.stereotype.Component;

@Component
public class TreeNodeFetcherImpl implements TreeNodeFetcher {
	private final TreeNodeFactory treeNodeFactory;
	private final PrefixConstructor prefixConstructor;

	public TreeNodeFetcherImpl(TreeNodeFactory treeNodeFactory, PrefixConstructor prefixConstructor) {
		this.treeNodeFactory = treeNodeFactory;
		this.prefixConstructor = prefixConstructor;
	}

	@Override
	public TreeNode getFragment(LdesFragmentRequest ldesFragmentRequest) {
		final ViewName viewName = ldesFragmentRequest.viewName();
		return treeNodeFactory
				.getTreeNode(new Fragment(
						new LdesFragmentIdentifier(viewName, ldesFragmentRequest.fragmentPairs())).getFragmentId(),
						prefixConstructor.buildPrefix(), viewName.getCollectionName());
	}
}
