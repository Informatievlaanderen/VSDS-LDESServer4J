package be.vlaanderen.informatievlaanderen.ldes.server.retention;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.mapper.MemberPropertiesEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class MemberPropertiesRepositoryImpl implements MemberPropertiesRepository {

	public static final String VIEWS = "views";
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
	public void save(MemberProperties memberProperties) {
		memberPropertiesEntityRepository.save(memberPropertiesEntityMapper.toMemberEntity(memberProperties));
	}

	@Override
	public Optional<MemberProperties> retrieve(String id) {
		return memberPropertiesEntityRepository.findById(id).map(memberPropertiesEntityMapper::toMember);
	}

	@Override
	public synchronized void addViewReference(String id, String viewName) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		Update update = new Update();
		update.push(VIEWS, viewName);
		mongoTemplate.upsert(query, update, MemberPropertiesEntity.class);
	}

	@Override
	public List<MemberProperties> getMemberPropertiesOfVersion(String versionOf) {
		// TODO Implement
		return null;
	}

	@Override
	public Stream<MemberProperties> getMemberPropertiesWithViewReference(String viewName) {
		// TODO Implement
		return null;
	}

	@Override
	public void removeViewReference(String id, String viewName) {
		// TODO Implement
	}

	@Override
	public void deleteById(String id) {
		// TODO Implement
	}
}
