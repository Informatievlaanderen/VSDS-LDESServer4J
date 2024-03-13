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

	void incrementNrOfMembersAdded(LdesFragmentIdentifier fragmentId);
	void incrementNrOfMembersAdded(LdesFragmentIdentifier fragmentId, int size);

	Stream<Fragment> retrieveFragmentsOfView(String defaultViewName);

	void removeLdesFragmentsOfView(String viewName);

	void deleteTreeNodesByCollection(String collectionName);

	/**
	 * Returns all the Fragments that have a relation defined where the treeNode is
	 * the given ldesFragmentIdentifier
	 * <p>
	 * Example:
	 * <ul>
	 * <li>FragmentA has one relation towards FragmentC</li>
	 * <li>FragmentB has two relations, one towards FragmentC and one towards
	 * FragmentD</li>
	 * <li>FragmentC and FragmentD have no relations</li>
	 * </ul>
	 * <p>
	 * In this case:
	 * <ul>
	 * <li>retrieveFragmentsByOutgoingRelation(FragmentA) would return an empty
	 * List</li>
	 * <li>retrieveFragmentsByOutgoingRelation(FragmentB) would return an empty
	 * List</li>
	 * <li>retrieveFragmentsByOutgoingRelation(FragmentC) would return a List
	 * consisting of FragmentA and FragmentB</li>
	 * <li>retrieveFragmentsByOutgoingRelation(FragmentD) would return a List
	 * consisting of FragmentB</li>
	 * </ul>
	 */
	List<Fragment> retrieveFragmentsByOutgoingRelation(LdesFragmentIdentifier ldesFragmentIdentifier);

	Stream<Fragment> getDeletionCandidates();

	void removeRelationsPointingToFragmentAndDeleteFragment(LdesFragmentIdentifier readyForDeletionFragment);

	/**
	 * Finds and updates all the child relations of the fragment and makes them immutable.
	 * For example fragment year=2024, all fragments referring to months, days, hours, etc. within 2024 will become immutable.
	 * The fragment itself does NOT become immutable. Only the children
	 * When the fragment has no children, or only has immutable children, then no updates are done.
	 * @param fragment The fragment whose children should become immutable
	 */
    void makeChildrenImmutable(Fragment fragment);
}
