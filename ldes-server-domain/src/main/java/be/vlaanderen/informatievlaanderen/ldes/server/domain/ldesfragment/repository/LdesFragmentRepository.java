package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface LdesFragmentRepository {
	LdesFragment saveFragment(LdesFragment ldesFragment);

	Optional<LdesFragment> retrieveFragment(LdesFragmentRequest ldesFragmentRequest);

	Optional<LdesFragment> retrieveOpenChildFragment(String parentId);

	Optional<LdesFragment> retrieveMutableFragment(String viewName,
			List<FragmentPair> fragmentPairList);

	Optional<LdesFragment> retrieveRootFragment(String viewName);

	Stream<LdesFragment> retrieveNonDeletedImmutableFragmentsOfView(String viewName);

	Optional<LdesFragment> retrieveNonDeletedChildFragment(String viewName,
			List<FragmentPair> fragmentPairList);

	void addMemberToFragment(LdesFragment ldesFragment, String memberId);

	void incrementNumberOfMembers(String fragmentId);
}
