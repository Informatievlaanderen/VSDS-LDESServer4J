package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import org.springframework.stereotype.Component;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.FragmentConstants.*;

@Component
public class RootFragmentService {
	private final LdesFragmentRepository ldesFragmentRepository;

	private final LdesConfig ldesConfig;

	public RootFragmentService(LdesFragmentRepository ldesFragmentRepository, LdesConfig ldesConfig) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.ldesConfig = ldesConfig;
	}

	public LdesFragment getRootFragment() {
		return ldesFragmentRepository
				.retrieveFragment(new LdesFragmentRequest(ldesConfig.getCollectionName(),
						List.of(new FragmentPair(FRAGMENT_KEY, FRAGMENT_VALUE_ROOT))))
				.orElseGet(this::createNewRootFragment);

	}

	public void addRelationToParentFragment(LdesFragment parentFragment, List<LdesFragment> ldesFragments) {
		ldesFragments.stream()
				.filter(ldesFragment -> !ldesFragment.getMemberIds().isEmpty())
				.forEach(ldesFragment -> {
					parentFragment.addRelation(
							new TreeRelation(null, ldesFragment.getFragmentId(), null, null, TREE_RELATION));
				});
		// parentFragment.clearMembers();
		ldesFragmentRepository.saveFragment(parentFragment);
	}

	private LdesFragment createNewRootFragment() {
		FragmentInfo fragmentInfo = new FragmentInfo(
				ldesConfig.getCollectionName(),
				List.of(new FragmentPair(FRAGMENT_KEY, FRAGMENT_VALUE_ROOT)));

		return new LdesFragment(ldesConfig.getHostName() + "/" + fragmentInfo.getCollectionName(),
				fragmentInfo);
	}

}
