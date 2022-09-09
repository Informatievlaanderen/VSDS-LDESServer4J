package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;

import java.util.List;
import java.util.Optional;

public interface LdesFragmentRepository {
	LdesFragment saveFragment(LdesFragment ldesFragment);

	Optional<LdesFragment> retrieveFragment(LdesFragmentRequest ldesFragmentRequest);

	Optional<LdesFragment> retrieveChildFragment(String viewName,
			List<FragmentPair> fragmentPairList);

	Optional<LdesFragment> retrieveOpenFragment(String viewName,
			List<FragmentPair> fragmentPairList);

	List<LdesFragment> retrieveAllFragments();
}
