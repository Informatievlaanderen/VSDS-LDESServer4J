package be.vlaanderen.informatievlaanderen.ldes.server.retention;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.mapper.MemberPropertiesEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
public class MemberPropertiesRepositoryImpl implements MemberPropertiesRepository {

	private static final Logger log = LoggerFactory.getLogger(MemberPropertiesRepositoryImpl.class);

	public static final String DOCUMENTS = "documents";
	public static final String ID = "_id";
	public static final String VIEWS = "views";
	private static final String COLLECTION = "collectionName";
	private static final String VERSION_OF = "versionOf";
	private static final String TIMESTAMP = "timestamp";

	private final MemberPropertiesEntityRepository memberPropertiesEntityRepository;
	private final MemberPropertiesEntityMapper memberPropertiesEntityMapper;
	private final MongoTemplate mongoTemplate;

	public MemberPropertiesRepositoryImpl(MemberPropertiesEntityRepository memberPropertiesEntityRepository,
			MemberPropertiesEntityMapper memberPropertiesEntityMapper, MongoTemplate mongoTemplate) {
		this.memberPropertiesEntityRepository = memberPropertiesEntityRepository;
		this.memberPropertiesEntityMapper = memberPropertiesEntityMapper;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void insert(MemberProperties memberProperties) {
		memberPropertiesEntityRepository.insert(memberPropertiesEntityMapper.toMemberPropertiesEntity(memberProperties));
	}

	@Override
	public Optional<MemberProperties> retrieve(String id) {
		return memberPropertiesEntityRepository.findById(id).map(memberPropertiesEntityMapper::toMemberProperties);
	}

	@Override
	public void addViewToAll(ViewName viewName) {
		final var query = new Query();
		query.addCriteria(Criteria.where(COLLECTION).is(viewName.getCollectionName()));
		final var update = new Update().addToSet(VIEWS, viewName.asString());
		final var bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MemberPropertiesEntity.class);
		bulkOps.updateMulti(query, update);

		final var result = bulkOps.execute();
		log.atInfo().log("View {} added to {} MemberProperties", viewName.asString(), result.getModifiedCount());
	}

	@Override
	public List<MemberProperties> getMemberPropertiesOfVersionAndView(String versionOf, String viewName) {
		Query query = new Query();
		query.addCriteria(Criteria.where(VIEWS).is(viewName).and(VERSION_OF).is(versionOf));
		return mongoTemplate
				.stream(query, MemberPropertiesEntity.class)
				.map(memberPropertiesEntityMapper::toMemberProperties)
				.toList();
	}

	@Override
	public Stream<MemberProperties> getMemberPropertiesWithViewReference(ViewName viewName) {
		Query query = new Query();
		query.addCriteria(Criteria.where(VIEWS).is(viewName.asString()));
		return mongoTemplate.stream(query, MemberPropertiesEntity.class)
				.map(memberPropertiesEntityMapper::toMemberProperties);
	}

	@Override
	public void removeViewReference(String id, String viewName) {
		Query query = new Query();
		query.addCriteria(Criteria.where(ID).is(id));
		Update update = new Update();
		update.pull(VIEWS, viewName);
		mongoTemplate.upsert(query, update, MemberPropertiesEntity.class);
	}

	@Override
	public void removeMemberPropertiesOfCollection(String collectionName) {
		Query query = new Query();
		query.addCriteria(Criteria.where(COLLECTION).is(collectionName));
		mongoTemplate.remove(query, MemberPropertiesEntity.class);
	}

	@Override
	public void deleteById(String id) {
		memberPropertiesEntityRepository.deleteById(id);
	}

	@Override
	public Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName,
																TimeBasedRetentionPolicy policy) {
		return memberPropertiesEntityRepository
				.findMemberPropertiesEntitiesByCollectionNameAndViewsContainingAndTimestampBefore(
						viewName.getCollectionName(),
						viewName.asString(),
						LocalDateTime.now().minus(policy.duration())
				)
				.map(memberPropertiesEntityMapper::toMemberProperties);
	}

	@Override
	public Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName,
																VersionBasedRetentionPolicy policy) {
		int versionsToKeep = policy.numberOfMembersToKeep();
		String collectionName = viewName.getCollectionName();
		String viewNameAsString = viewName.asString();

		final SortOperation sort = sortTimestampDesc();
		final MatchOperation match =
				match(Criteria.where(COLLECTION).is(collectionName).and(VIEWS).in(viewNameAsString));
		final GroupOperation group = groupOnVersionOf();
		final ProjectionOperation project = projectVersionsToKeep(versionsToKeep);
		final UnwindOperation unwind = unwindDocuments();
		final ReplaceRootOperation replaceRoot = getReplaceRootDocuments();

		final Aggregation aggregation = createAggregation(
				sort, match, group, project, unwind, replaceRoot
		);

		return mongoTemplate.aggregateStream(aggregation, MemberPropertiesEntity.NAME, MemberProperties.class);
	}

	@Override
	public Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName,
																TimeAndVersionBasedRetentionPolicy policy) {
		final MatchOperation match =
				match(Criteria
						.where(COLLECTION).is(viewName.getCollectionName())
						.and(VIEWS).in(viewName.asString())
						.and(TIMESTAMP).lt(LocalDateTime.now().minus(policy.duration())));

		final Aggregation aggregation = createAggregation(
				sortTimestampDesc(),
				match,
				groupOnVersionOf(),
				projectVersionsToKeep(policy.numberOfMembersToKeep()),
				unwindDocuments(),
				getReplaceRootDocuments()
		);

		return mongoTemplate.aggregateStream(aggregation, MemberPropertiesEntity.NAME, MemberProperties.class);
	}

	private Aggregation createAggregation(AggregationOperation... operations) {
		final AggregationOptions aggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
		return newAggregation(operations).withOptions(aggregationOptions);
	}

	private ReplaceRootOperation getReplaceRootDocuments() {
		return replaceRoot(DOCUMENTS);
	}

	private UnwindOperation unwindDocuments() {
		return unwind(DOCUMENTS);
	}

	private ProjectionOperation projectVersionsToKeep(int versionsToKeep) {
		return project().and(DOCUMENTS).slice(Integer.MAX_VALUE, versionsToKeep).as(DOCUMENTS);
	}

	private GroupOperation groupOnVersionOf() {
		return group(VERSION_OF).push("$$ROOT").as(DOCUMENTS);
	}

	private SortOperation sortTimestampDesc() {
		return sort(Sort.Direction.DESC, TIMESTAMP);
	}

}
