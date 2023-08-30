package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FragmentCompactionService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FragmentCompactionService.class);

	public static final String PAGE_NUMBER_KEY = "pageNumber";
	private final FragmentRepository fragmentRepository;
	private final CompactedFragmentCreator compactedFragmentCreator;

	public FragmentCompactionService(FragmentRepository fragmentRepository,
			CompactedFragmentCreator compactedFragmentCreator) {
		this.fragmentRepository = fragmentRepository;
		this.compactedFragmentCreator = compactedFragmentCreator;
	}

	public void compactFragments(Fragment firstFragment, Fragment secondFragment) {
		LdesFragmentIdentifier ldesFragmentIdentifier = generateNewLdesFragmentIdentifier(firstFragment,
				secondFragment);
		if (fragmentRepository.retrieveFragment(ldesFragmentIdentifier).isEmpty()) {
			compactedFragmentCreator.createCompactedFragment(firstFragment, secondFragment, ldesFragmentIdentifier);
		} else {
			String ldesFragmentIdentifierString = ldesFragmentIdentifier.asString();
			LOGGER.warn("Already created a compacted fragment with identifier {}", ldesFragmentIdentifierString);
		}

	}

	private LdesFragmentIdentifier generateNewLdesFragmentIdentifier(Fragment firstFragment, Fragment secondFragment) {
		List<FragmentPair> fragmentPairs = new ArrayList<>(firstFragment.getFragmentPairs());
		fragmentPairs.remove(fragmentPairs.size() - 1);
		fragmentPairs.add(
				new FragmentPair(PAGE_NUMBER_KEY, getPageNumber(firstFragment) + "/" + getPageNumber(secondFragment)));
		return new LdesFragmentIdentifier(firstFragment.getViewName(), fragmentPairs);
	}

	private String getPageNumber(Fragment firstFragment) {
		return firstFragment
				.getFragmentId()
				.getValueOfFragmentPairKey(PAGE_NUMBER_KEY)
				.orElseThrow(
						() -> new MissingFragmentValueException(firstFragment.getFragmentIdString(), PAGE_NUMBER_KEY));
	}
}
