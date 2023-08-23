package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface FragmentRepository {
	Fragment saveFragment(Fragment fragment);

	Optional<Fragment> retrieveFragment(LdesFragmentIdentifier fragmentId);

	Optional<Fragment> retrieveOpenChildFragment(LdesFragmentIdentifier parentId);

	Optional<Fragment> retrieveMutableFragment(String viewName,
			List<FragmentPair> fragmentPairList);

	Optional<Fragment> retrieveRootFragment(String viewName);

	void incrementNumberOfMembers(LdesFragmentIdentifier fragmentId);

	Stream<Fragment> retrieveFragmentsOfView(String defaultViewName);

	void removeLdesFragmentsOfView(String viewName);

	void deleteTreeNodesByCollection(String collectionName);

	List<Fragment> retrieveFragmentsByOutgoingRelation(LdesFragmentIdentifier fragmentId);
}
