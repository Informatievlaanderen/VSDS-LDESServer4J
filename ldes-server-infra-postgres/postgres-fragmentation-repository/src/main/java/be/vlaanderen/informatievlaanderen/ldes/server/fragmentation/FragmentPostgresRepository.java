package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.FragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.TreeRelationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class FragmentPostgresRepository implements FragmentRepository {

	private static final Logger log = LoggerFactory.getLogger(FragmentPostgresRepository.class);

	private final FragmentEntityRepository repository;

	public FragmentPostgresRepository(FragmentEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public Fragment saveFragment(Fragment fragment) {
		repository.save(FragmentEntity.fromLdesFragment(fragment));
		return fragment;
	}

	@Override
	public Optional<Fragment> retrieveFragment(LdesFragmentIdentifier fragmentId) {
		return repository
				.findById(fragmentId.asDecodedFragmentId())
				.map(FragmentEntity::toLdesFragment);
	}

	@Override
	public Optional<Fragment> retrieveMutableFragment(String viewName,
			List<FragmentPair> fragmentPairList) {
		return repository
				.findAllByImmutableAndViewName(false,
						viewName)
				.stream()
				.map(FragmentEntity::toLdesFragment)
				.min(Comparator.comparing(Fragment::getFragmentIdString));
	}

	@Override
	public Optional<Fragment> retrieveOpenChildFragment(LdesFragmentIdentifier parentId) {
		return repository
				.findByImmutableAndParentId(false,
						parentId.asDecodedFragmentId())
				.map(FragmentEntity::toLdesFragment);
	}

	@Override
	public Optional<Fragment> retrieveRootFragment(String viewName) {
		return repository
				.findLdesFragmentEntityByRootAndViewName(true, viewName)
				.map(FragmentEntity::toLdesFragment);
	}

	@Override
	@Transactional
	public void incrementNrOfMembersAdded(LdesFragmentIdentifier fragmentId) {
		repository.incrementNrOfMembersAdded(fragmentId.asDecodedFragmentId(), 1);
	}

	@Override
	public void incrementNrOfMembersAdded(LdesFragmentIdentifier fragmentId, int size) {
		repository.incrementNrOfMembersAdded(fragmentId.asDecodedFragmentId(), size);
	}

	@Override
	public Stream<Fragment> retrieveFragmentsOfView(String viewName) {
		return repository
				.findAllByViewName(viewName)
				.map(FragmentEntity::toLdesFragment);
	}

	@Override
	public void removeLdesFragmentsOfView(String viewName) {
		int deleteCount = repository.removeByViewName(viewName).size();
		log.debug("Deleted {} treeNodes", deleteCount);
	}

	@Override
	@Transactional
	public void deleteTreeNodesByCollection(String collectionName) {
		Long deleteCount = repository.deleteAllByCollectionName(collectionName);
		log.debug("Deleted {} treeNodes", deleteCount);
	}

	@Override
	public List<Fragment> retrieveFragmentsByOutgoingRelation(LdesFragmentIdentifier ldesFragmentIdentifier) {
		return repository
				.findAllByRelations_TreeNode(ldesFragmentIdentifier.asDecodedFragmentId())
				.stream()
				.map(FragmentEntity::toLdesFragment)
				.toList();
	}

	@Override
	public Stream<Fragment> getDeletionCandidates() {
		return repository
				.findByDeleteTimeNotNull()
				.map(FragmentEntity::toLdesFragment);
	}

	@Override
	@Transactional
	public void removeRelationsPointingToFragmentAndDeleteFragment(LdesFragmentIdentifier readyForDeletionFragmentId) {
		removeRelationsPointingToDeletedFragment(readyForDeletionFragmentId);
		repository.deleteById(readyForDeletionFragmentId.asDecodedFragmentId());
	}

    @Override
    public void makeChildrenImmutable(Fragment fragment) {
		int modifiedRows = repository.closeChildren(fragment.getFragmentIdString());
		log.atInfo().log("{} child/children of {} was/were made immutable.", modifiedRows, fragment.getFragmentIdString());
    }

    private void removeRelationsPointingToDeletedFragment(LdesFragmentIdentifier readyForDeletionFragmentId) {
		List<FragmentEntity> fragments = repository.findAllByRelations_TreeNode(readyForDeletionFragmentId.asDecodedFragmentId());
		fragments.forEach(fragment -> {
			List<TreeRelationEntity> relationsToRemove = fragment.getRelations().stream()
					.filter(treeRelation -> treeRelation.getTreeNode()
							.equals(readyForDeletionFragmentId.asDecodedFragmentId()))
					.toList();
			relationsToRemove.forEach(fragment::removeRelation);
		});
		repository.saveAll(new HashSet<>(fragments));
	}

}
