package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.events.FragmentsCompactedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FragmentCompactionService {
	private final FragmentRepository fragmentRepository;
	private final CompactedFragmentCreator compactedFragmentCreator;
	private final ApplicationEventPublisher applicationEventPublisher;

	public FragmentCompactionService(FragmentRepository fragmentRepository,
			CompactedFragmentCreator compactedFragmentCreator, ApplicationEventPublisher applicationEventPublisher) {
		this.fragmentRepository = fragmentRepository;
		this.compactedFragmentCreator = compactedFragmentCreator;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public void compactFragments(Fragment firstFragment, Fragment secondFragment) {
		LdesFragmentIdentifier ldesFragmentIdentifier = generateNewLdesFragmentIdentifier(firstFragment,
				secondFragment);
		if (fragmentRepository.retrieveFragment(ldesFragmentIdentifier).isEmpty()) {
			compactedFragmentCreator.createCompactedFragment(firstFragment, secondFragment, ldesFragmentIdentifier
			);
			applicationEventPublisher.publishEvent(new FragmentsCompactedEvent(firstFragment, secondFragment));
		}

	}

	private LdesFragmentIdentifier generateNewLdesFragmentIdentifier(Fragment firstFragment, Fragment secondFragment) {
		List<FragmentPair> fragmentPairs = new ArrayList<>(firstFragment.getFragmentPairs());
		fragmentPairs.remove(fragmentPairs.size() - 1);
		fragmentPairs.add(
				new FragmentPair("pageNumber", getPageNumber(firstFragment) + "/" + getPageNumber(secondFragment)));
		return new LdesFragmentIdentifier(firstFragment.getViewName(), fragmentPairs);
	}

	private String getPageNumber(Fragment firstFragment) {
		return firstFragment.getFragmentPairs().get(firstFragment.getFragmentPairs().size() - 1).fragmentValue();
	}
}
