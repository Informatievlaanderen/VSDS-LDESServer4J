package be.vlaanderen.informatievlaanderen.ldes.server.fetch;

import be.vlaanderen.informatievlaanderen.ldes.server.fetch.mapper.MemberAllocationEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.repository.AllocationEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllocationMongoRepository implements AllocationRepository {

	private final AllocationEntityRepository repository;
	private final MemberAllocationEntityMapper mapper;

	public AllocationMongoRepository(AllocationEntityRepository repository, MemberAllocationEntityMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	public void saveAllocation(MemberAllocation memberAllocation) {
		repository.save(mapper.toMemberAllocationEntity(memberAllocation));
	}

	public void deleteByMemberIdAndCollectionNameAndViewName(String memberId, String collectionName, String viewName) {
		repository.deleteByMemberIdAndCollectionNameAndViewName(memberId, collectionName, viewName);
	}

	public List<MemberAllocation> getMemberAllocationsByFragmentId(String fragmentId) {
		return repository.findAllByFragmentId(fragmentId)
				.stream()
				.map(mapper::toMemberAllocation)
				.toList();
	}

	@Override
	public long countByCollectionNameAndViewName(String collectionName, String viewName) {
		// TODO: 04/12/23 Desactivated due to performance issues on the count query
		// refer to: https://github.com/Informatievlaanderen/VSDS-LDESServer4J/issues/1028
		// Normally we do not query this anymore but this was disabled in a rush and I want to be really really sure.
		return -1L;
//		return repository.countByCollectionNameAndViewName(collectionName, viewName);
	}

	public void deleteByCollectionNameAndViewName(String collectionName, String viewName) {
		repository.deleteAllByCollectionNameAndViewName(collectionName, viewName);
	}

	@Override
	public void deleteByFragmentId(String fragmentId) {
		repository.deleteAllByFragmentId(fragmentId);
	}

	public void deleteByCollectionName(String collectionName) {
		repository.deleteAllByCollectionName(collectionName);
	}

}
