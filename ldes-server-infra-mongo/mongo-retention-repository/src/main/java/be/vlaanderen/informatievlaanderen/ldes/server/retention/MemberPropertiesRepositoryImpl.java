package be.vlaanderen.informatievlaanderen.ldes.server.retention;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.mapper.MemberPropertiesEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
	public synchronized void allocateMember(String memberId, ViewName viewName) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(memberId));
		Update update = new Update();
		update.push(VIEWS, viewName.asString());
		mongoTemplate.upsert(query, update, MemberPropertiesEntity.class);
	}
}
