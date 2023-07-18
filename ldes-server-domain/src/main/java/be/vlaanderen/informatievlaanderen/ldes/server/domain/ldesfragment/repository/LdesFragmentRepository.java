package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface LdesFragmentRepository {
	LdesFragment saveFragment(LdesFragment ldesFragment);

	Optional<LdesFragment> retrieveFragment(String fragmentId);

	Optional<LdesFragment> retrieveOpenChildFragment(String parentId);

	Optional<LdesFragment> retrieveMutableFragment(String viewName,
			List<FragmentPair> fragmentPairList);

	Optional<LdesFragment> retrieveRootFragment(String viewName);

	void incrementNumberOfMembers(String fragmentId);

	Stream<LdesFragment> retrieveFragmentsOfView(String defaultViewName);

	void removeLdesFragmentsOfView(String viewName);

	void deleteTreeNodesByCollection(String collectionName);
}
