package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface LdesFragmentRepository {
	LdesFragment saveFragment(LdesFragment ldesFragment);

	boolean removeRelationFromFragment(LdesFragment fragment, TreeRelation treeRelation);

	boolean addRelationToFragment(LdesFragment fragment, TreeRelation treeRelation);

	boolean closeFragmentAndAddNewRelation(LdesFragment completeFragment, TreeRelation treeRelation);

	boolean setSoftDeleted(LdesFragment fragment);

	Optional<LdesFragment> retrieveFragment(LdesFragmentRequest ldesFragmentRequest);

	Optional<LdesFragment> retrieveOpenChildFragment(String viewName,
			List<FragmentPair> fragmentPairList);

	Optional<LdesFragment> retrieveMutableFragment(String viewName,
			List<FragmentPair> fragmentPairList);

	Optional<LdesFragment> retrieveRootFragment(String viewName);

	Stream<LdesFragment> retrieveNonDeletedImmutableFragmentsOfView(String viewName);

	Optional<LdesFragment> retrieveNonDeletedChildFragment(String viewName,
			List<FragmentPair> fragmentPairList);

	boolean addMemberToFragment(LdesFragment ldesFragment, String memberId);
}
