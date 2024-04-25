package be.vlaanderen.informatievlaanderen.ldes.server.fetch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.mapper.MemberAllocationEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.repository.AllocationEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class AllocationPostgresRepository implements AllocationRepository {
	private final AllocationEntityRepository repository;
	private final MemberAllocationEntityMapper mapper = new MemberAllocationEntityMapper();

	public AllocationPostgresRepository(AllocationEntityRepository repository) {
		this.repository = repository;
	}

	public void saveAllocation(MemberAllocation memberAllocation) {
		repository.save(mapper.toMemberAllocationEntity(memberAllocation));
	}

	@Override
	public void saveAllocations(List<MemberAllocation> memberAllocations) {
		repository.saveAll(memberAllocations.stream().map(mapper::toMemberAllocationEntity).toList());
	}

	public void deleteByMemberIdAndCollectionNameAndViewName(String memberId, String collectionName, String viewName) {
		repository.deleteByMemberIdAndCollectionNameAndViewName(memberId, collectionName, viewName);
	}

	public Stream<MemberAllocation> getMemberAllocationsByFragmentId(String fragmentId) {
		return repository.findAllByFragmentId(fragmentId)
				.stream()
				.map(mapper::toMemberAllocation);
	}

	@Override
	public List<String> getMemberAllocationIdsByFragmentIds(Set<String> fragmentIds) {
		return repository.findDistinctMemberIdsByFragmentIds(fragmentIds);
	}

	public void deleteByCollectionNameAndViewName(String collectionName, String viewName) {
		repository.deleteAllByCollectionNameAndViewName(collectionName, viewName);
	}

	@Override
	public void deleteAllByFragmentId(Set<String> fragmentIds) {
		repository.deleteAllByFragmentIdIn(fragmentIds);
	}

	@Override
	public Stream<CompactionCandidate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage) {
		return repository.findCompactionCandidates(viewName.getCollectionName(), viewName.getViewName(), capacityPerPage);
	}

	@Override
	public void deleteByCollectionName(String collectionName) {
		repository.deleteAllByCollectionName(collectionName);
	}

}
