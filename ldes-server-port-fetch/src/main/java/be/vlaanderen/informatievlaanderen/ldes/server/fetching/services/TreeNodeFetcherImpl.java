package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.TreeNodeRepository;
import org.springframework.stereotype.Component;

@Component
public class TreeNodeFetcherImpl implements TreeNodeFetcher {
	private final TreeNodeRepository treeNodeRepository;

	public TreeNodeFetcherImpl(TreeNodeRepository treeNodeRepository) {
		this.treeNodeRepository = treeNodeRepository;
	}

	@Override
	public TreeNode getFragment(LdesFragmentRequest ldesFragmentRequest) {
		final ViewName viewName = ldesFragmentRequest.viewName();
		final LdesFragmentIdentifier ldesFragmentIdentifier = new LdesFragmentIdentifier(ldesFragmentRequest.viewName(), ldesFragmentRequest.fragmentPairs());
		return treeNodeRepository
				.findByFragmentIdentifier(new LdesFragmentIdentifier(viewName, ldesFragmentRequest.fragmentPairs()))
				.orElseThrow(() -> new MissingResourceException("TreeNode", ldesFragmentIdentifier.asDecodedFragmentId()));
	}
}
