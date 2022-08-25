package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;

import java.util.List;
import java.util.Optional;

public interface LdesFragmentRepository {
	LdesFragment saveFragment(LdesFragment ldesFragment);

	Optional<LdesFragment> retrieveFragment(LdesFragmentRequest ldesFragmentRequest);

	Optional<LdesFragment> retrieveChildFragment(String collectionName, List<FragmentPair> fragmentPairList);

	Optional<LdesFragment> retrieveOpenFragment(String collectionName, List<FragmentPair> fragmentPairList);

	Optional<LdesFragment> retrieveInitialFragment(String collectionName);

	List<LdesFragment> retrieveAllFragments();
}
