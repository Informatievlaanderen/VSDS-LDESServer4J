package be.vlaanderen.informatievlaanderen.ldes.server.fetch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.mapper.MemberAllocationEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.repository.AllocationEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.AllocationAggregate;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
public class AllocationMongoRepository implements AllocationRepository {

	private final AllocationEntityRepository repository;
	private final MemberAllocationEntityMapper mapper;
	private final MongoTemplate mongoTemplate;

	public AllocationMongoRepository(AllocationEntityRepository repository, MemberAllocationEntityMapper mapper, MongoTemplate mongoTemplate) {
		this.repository = repository;
		this.mapper = mapper;
		this.mongoTemplate = mongoTemplate;
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
	public List<String> getMemberAllocationIdsByFragmentIds(Set<String> fragmentIds) {
		Query q = new Query();
		q.addCriteria(Criteria.where("fragmentId").in(fragmentIds))
				.fields().include("memberId");
		return mongoTemplate.find(q, MemberAllocationEntity.class)
				.stream()
				.map(MemberAllocationEntity::getMemberId)
				.toList();
	}

	@Override
	public long countByCollectionNameAndViewName(String collectionName, String viewName) {
		// 04/12/23 Desactivated due to performance issues on the count query
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

	@Override
	public void deleteAllByFragmentId(Set<String> fragmentIds) {
		repository.deleteAllByFragmentIdIn(fragmentIds);
	}

	@Override
	public Stream<AllocationAggregate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage) {

		MatchOperation filterViewName = match(Criteria.where("collectionName").is(viewName.getCollectionName()).and("viewName").is(viewName.getViewName()));
		GroupOperation countMembersForFragmentId = group("fragmentId").count().as("size");
		MatchOperation filterOutFullAggregates = match(Criteria.where("size").lt(capacityPerPage));

		Aggregation aggregation =
				newAggregation(filterViewName, countMembersForFragmentId, filterOutFullAggregates)
						.withOptions(newAggregationOptions().allowDiskUse(true).build());

		return mongoTemplate.aggregateStream(aggregation, "fetch_allocation", AllocationAggregate.class);
	}

	public void deleteByCollectionName(String collectionName) {
		repository.deleteAllByCollectionName(collectionName);
	}

}
