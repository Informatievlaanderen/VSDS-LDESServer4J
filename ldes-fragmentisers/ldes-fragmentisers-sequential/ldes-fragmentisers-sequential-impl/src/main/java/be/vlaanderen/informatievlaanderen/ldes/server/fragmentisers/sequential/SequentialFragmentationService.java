package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.sequential;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.RootFragmentService;

public class SequentialFragmentationService implements FragmentationService {

	protected final LdesConfig ldesConfig;
	protected final FragmentCreator fragmentCreator;
	protected final LdesMemberRepository ldesMemberRepository;
	protected final LdesFragmentRepository ldesFragmentRepository;
	protected final RootFragmentService rootFragmentService;

	public SequentialFragmentationService(LdesConfig ldesConfig, FragmentCreator fragmentCreator,
			LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository,
			RootFragmentService rootFragmentService) {
		this.ldesConfig = ldesConfig;
		this.fragmentCreator = fragmentCreator;
		this.ldesMemberRepository = ldesMemberRepository;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.rootFragmentService = rootFragmentService;
	}

	@Override
	public List<LdesFragment> addMemberToFragment(List<LdesFragment> parentFragments, String ldesMemberId) {
		List<LdesFragment> modifiedLdesFragments;

		if (parentFragments.isEmpty()) {
			modifiedLdesFragments = fragmentMember(rootFragmentService.getRootFragment(), ldesMemberId);
		} else {
			modifiedLdesFragments = parentFragments.stream()
					.filter(ldesFragment -> !ldesFragment.getMemberIds().isEmpty())
					.flatMap(ldesFragment -> ldesFragment.getMemberIds().stream()
							.flatMap(member -> fragmentMember(ldesFragment, member).stream()))
					.toList();
		}
		parentFragments.forEach(ldesFragment -> {
			ldesFragment.clearMembers();
			ldesFragmentRepository.saveFragment(ldesFragment);
		});
		modifiedLdesFragments.forEach(ldesFragmentRepository::saveFragment);

		return modifiedLdesFragments;
	}

	private List<LdesFragment> fragmentMember(LdesFragment parentFragment, String ldesMemberId) {
		LdesMember ldesMember = ldesMemberRepository.getLdesMemberById(ldesMemberId)
				.orElseThrow(() -> new MemberNotFoundException(ldesMemberId));

		LdesFragment ldesFragment = retrieveLastFragmentOrCreateNewFragment();
		ldesFragment.addMember(ldesMember.getLdesMemberId());

		rootFragmentService.addRelationToParentFragment(parentFragment, List.of(ldesFragment));

		return List.of(ldesFragment);
	}

	private LdesFragment retrieveLastFragmentOrCreateNewFragment() {
		return ldesFragmentRepository
				.retrieveOpenFragment(ldesConfig.getCollectionName(), ldesConfig.getTimestampPath()).map(fragment -> {
					if (fragmentCreator.needsToCreateNewFragment(fragment)) {
						return fragmentCreator.createNewFragment(Optional.of(fragment), null);
					} else {
						return fragment;
					}
				}).orElseGet(() -> fragmentCreator.createNewFragment(Optional.empty(), null));
	}
}
