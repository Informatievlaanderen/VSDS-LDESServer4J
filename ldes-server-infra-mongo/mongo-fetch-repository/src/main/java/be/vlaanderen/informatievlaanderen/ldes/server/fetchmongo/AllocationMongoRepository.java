package be.vlaanderen.informatievlaanderen.ldes.server.fetchmongo;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchmongo.mapper.MemberAllocationEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchmongo.repository.AllocationEntityRepository;
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

	public void deleteByCollectionNameAndViewName(String collectionName, String viewName) {
		repository.deleteAllByCollectionNameAndViewName(collectionName, viewName);
	}

	public void deleteByCollectionName(String collectionName) {
		repository.deleteAllByCollectionName(collectionName);
	}

}
