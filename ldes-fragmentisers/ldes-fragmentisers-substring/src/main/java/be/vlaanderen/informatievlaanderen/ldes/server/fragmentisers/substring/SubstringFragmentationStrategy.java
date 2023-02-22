package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model.LocalMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model.LocalMemberSupplier;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import java.util.HashSet;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator.SUBSTRING;

public class SubstringFragmentationStrategy extends FragmentationStrategyDecorator {

	public static final String ROOT_SUBSTRING = "";

	private final ObservationRegistry observationRegistry;
	private final SubstringFragmentFinder substringFragmentFinder;
	private final SubstringFragmentCreator substringFragmentCreator;
	private final LocalMemberSupplier localMemberSupplier;

	public SubstringFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			ObservationRegistry observationRegistry,
			SubstringFragmentFinder substringFragmentFinder,
			SubstringFragmentCreator substringFragmentCreator,
			TreeRelationsRepository treeRelationsRepository,
			LocalMemberSupplier localMemberSupplier) {
		super(fragmentationStrategy, treeRelationsRepository);
		this.observationRegistry = observationRegistry;
		this.substringFragmentFinder = substringFragmentFinder;
		this.substringFragmentCreator = substringFragmentCreator;
		this.localMemberSupplier = localMemberSupplier;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Observation parentObservation) {
		final Observation substringFragmentationObservation = startFragmentationObservation(parentObservation);

		final LocalMember localMember = localMemberSupplier.toLocalMember(member);
		final LdesFragment rootFragment = prepareRootFragment(parentFragment);
		addMemberToFragments(parentFragment, localMember, substringFragmentationObservation, rootFragment);
		substringFragmentationObservation.stop();
	}

	private LdesFragment prepareRootFragment(LdesFragment parentFragment) {
		final LdesFragment rootFragment = substringFragmentCreator.getOrCreateSubstringFragment(parentFragment,
				ROOT_SUBSTRING);
		super.addRelationFromParentToChild(parentFragment, rootFragment);
		return rootFragment;
	}

	private void addMemberToFragments(LdesFragment parentFragment,
			LocalMember member,
			Observation substringFragmentationObservation,
			LdesFragment rootFragment) {
		final Set<String> substringsThatContainMember = new HashSet<>();
		member.getTokens().stream()
				.filter(token -> token.hasNotBeenAdded(substringsThatContainMember))
				.map(token -> {
					final LdesFragment substringFragment = substringFragmentFinder
							.getOpenOrLastPossibleFragment(parentFragment, rootFragment, token.getBucket());
					substringsThatContainMember.add(substringFragment.getValueOfKey(SUBSTRING).orElse(ROOT_SUBSTRING));
					return substringFragment;
				}).toList()
				.parallelStream()
				.forEach(substringFragment -> super.addMemberToFragment(substringFragment, member,
						substringFragmentationObservation));
	}

	private Observation startFragmentationObservation(Observation parentObservation) {
		return Observation.createNotStarted("substring fragmentation", observationRegistry)
				.parentObservation(parentObservation)
				.start();
	}

}
