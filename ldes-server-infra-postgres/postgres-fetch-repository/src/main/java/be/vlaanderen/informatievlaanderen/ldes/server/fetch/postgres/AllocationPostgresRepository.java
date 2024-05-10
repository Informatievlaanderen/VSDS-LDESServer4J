package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.mapper.MemberAllocationEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.repository.AllocationEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class AllocationPostgresRepository implements AllocationRepository {
	private final AllocationEntityRepository repository;
	private final EntityManager entityManager;
	private final MemberAllocationEntityMapper mapper = new MemberAllocationEntityMapper();

	public AllocationPostgresRepository(AllocationEntityRepository repository, EntityManager entityManager) {
		this.repository = repository;
		this.entityManager = entityManager;
	}

	@Transactional
	public void saveAllocation(MemberAllocation memberAllocation) {
		entityManager.persist(mapper.toMemberAllocationEntity(memberAllocation));
	}

	@Override
	@Transactional
	public void saveAllocations(List<MemberAllocation> memberAllocations) {
		memberAllocations.stream()
				.map(mapper::toMemberAllocationEntity)
				.forEach(entityManager::persist);
	}

	@Override
	@Transactional
	public void deleteByMemberIdAndCollectionNameAndViewName(String memberId, String collectionName, String viewName) {
		repository.deleteByMemberIdAndCollectionNameAndViewName(memberId, collectionName, viewName);
	}

	@Override
	public Stream<MemberAllocation> getMemberAllocationsByFragmentId(String fragmentId) {
		return repository.findAllByFragmentId(fragmentId)
				.stream()
				.map(mapper::toMemberAllocation);
	}

	@Override
	public List<String> getMemberAllocationIdsByFragmentIds(Set<String> fragmentIds) {
		return repository.findDistinctMemberIdsByFragmentIds(fragmentIds);
	}

	@Override
	@Transactional
	public void deleteByCollectionNameAndViewName(String collectionName, String viewName) {
		repository.deleteAllByCollectionNameAndViewName(collectionName, viewName);
	}

	@Override
	@Transactional
	public void deleteAllByFragmentId(Set<String> fragmentIds) {
		repository.deleteAllByFragmentIdIn(fragmentIds);
	}

	@Override
	@Transactional
	public Stream<CompactionCandidate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage) {
		return repository.findCompactionCandidates(viewName.getCollectionName(), viewName.getViewName(), capacityPerPage)
				.map(projection -> new CompactionCandidate(projection.getFragmentId(), projection.getSize()));
	}

	@Override
	@Transactional
	public void deleteByCollectionName(String collectionName) {
		repository.deleteAllByCollectionName(collectionName);
	}

}
