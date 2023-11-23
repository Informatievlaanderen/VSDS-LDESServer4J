package be.vlaanderen.informatievlaanderen.ldes.server.retention;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.mapper.MemberPropertiesEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import org.apache.jena.riot.system.IteratorStreamRDFText;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class MemberPropertiesRepositoryImpl implements MemberPropertiesRepository {

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
	public void saveMemberPropertiesWithoutViews(MemberProperties memberProperties) {
		Query query = new Query();
		query.addCriteria(Criteria.where(ID).is(memberProperties.getId()));
		Update update = new Update();
		update.set(COLLECTION, memberProperties.getCollectionName());
		update.set(VERSION_OF, memberProperties.getVersionOf());
		update.set(TIMESTAMP, memberProperties.getTimestamp());
		mongoTemplate.upsert(query, update, MemberPropertiesEntity.class);
	}

	@Override
	public void save(MemberProperties memberProperties) {
		memberPropertiesEntityRepository.save(memberPropertiesEntityMapper.toMemberPropertiesEntity(memberProperties));
	}

	@Override
	public Optional<MemberProperties> retrieve(String id) {
		return memberPropertiesEntityRepository.findById(id).map(memberPropertiesEntityMapper::toMemberProperties);
	}

	@Override
	public synchronized void addViewReference(String id, String viewName) {
		Query query = new Query();
		query.addCriteria(Criteria.where(ID).is(id));
		Update update = new Update();
		update.addToSet(VIEWS, viewName);
		mongoTemplate.upsert(query, update, MemberPropertiesEntity.class);
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

	// TODO TVB: 23/11/23 test me
	@Override
	public Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName,
																TimeBasedRetentionPolicy policy) {
		return memberPropertiesEntityRepository
				.findMemberPropertiesEntitiesByTimestampBefore(LocalDateTime.now().minus(policy.getDuration()))
				.map(memberPropertiesEntityMapper::toMemberProperties);
	}

	// TODO TVB: 23/11/23 cleanup and test
	@Override
	public Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName,
																VersionBasedRetentionPolicy policy) {
		int versionsToKeep = policy.getNumberOfMembersToKeep();
		String collectionName = viewName.getCollectionName();
		String viewNameAsString = viewName.asString();

		SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "timestamp");
		GroupOperation group = Aggregation.group("versionOf").push("$$ROOT").as("documents");
		ProjectionOperation project = Aggregation.project().and("documents").slice(Integer.MAX_VALUE, versionsToKeep).as("documents");
		UnwindOperation unwind = Aggregation.unwind("documents");
		ReplaceRootOperation replaceRoot = Aggregation.replaceRoot("documents");
		AggregationOptions aggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
		MatchOperation match = Aggregation.match(Criteria.where("collectionName").is(collectionName).and("views").in(viewNameAsString));
		Aggregation aggregation = Aggregation.newAggregation(sort, match, group, project, unwind, replaceRoot).withOptions(aggregationOptions);

		return mongoTemplate.aggregateStream(aggregation, "retention_member_properties", MemberProperties.class);
	}

	// TODO TVB: 23/11/23 test moi
	public Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName,
																Duration duration,
																int versionsToKeep) {
		// TODO TVB: 23/11/23 impl me, mongo query already ready
		return Stream.empty();
	}

}
