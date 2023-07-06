package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface FragmentRepository {
	LdesFragment saveFragment(LdesFragment ldesFragment);

	Optional<LdesFragment> retrieveFragment(LdesFragmentIdentifier fragmentId);

	Optional<LdesFragment> retrieveOpenChildFragment(LdesFragmentIdentifier parentId);

	Optional<LdesFragment> retrieveMutableFragment(String viewName,
			List<FragmentPair> fragmentPairList);

	Optional<LdesFragment> retrieveRootFragment(String viewName);

	void incrementNumberOfMembers(LdesFragmentIdentifier fragmentId);

	Stream<LdesFragment> retrieveFragmentsOfView(String defaultViewName);

	void removeLdesFragmentsOfView(String viewName);

	void deleteTreeNodesByCollection(String collectionName);
}
