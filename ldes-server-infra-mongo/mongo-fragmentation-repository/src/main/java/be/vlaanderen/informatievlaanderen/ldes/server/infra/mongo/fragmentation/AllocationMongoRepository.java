package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.AllocationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.AllocationEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllocationMongoRepository implements AllocationRepository {

	private static final Logger log = LoggerFactory.getLogger(AllocationMongoRepository.class);

	private final AllocationEntityRepository repository;

	public AllocationMongoRepository(AllocationEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public void allocateMemberToFragment(String memberId, ViewName viewName, String fragmentId) {
		repository.save(new AllocationEntity(new AllocationEntity.AllocationKey(memberId, viewName), fragmentId));
	}

	@Override
	public void unallocateMemberFromView(String memberId, ViewName viewName) {
		repository.deleteById(new AllocationEntity.AllocationKey(memberId, viewName));
	}
}
