package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset1;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.entity.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.entity.LdesMemberEntity;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.MemberMongoRepository.TREE_NODE_REFERENCES;

@ChangeUnit(id = "fragment-updater-changeset-1", order = "2", author = "VSDS")
public class FragmentUpdaterChange {
	private final MongoTemplate mongoTemplate;

	public FragmentUpdaterChange(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * This is the method with the migration code
	 **/
	@Execution
	public void changeSet() {
		List<LdesFragmentEntityV1> fragmentEntityV1s = mongoTemplate.find(new Query(), LdesFragmentEntityV1.class);
		fragmentEntityV1s.forEach(ldesFragmentEntityV1 -> {
			String parentId = getParentId(ldesFragmentEntityV1.getFragmentPairs(), ldesFragmentEntityV1.getViewName());
			LocalDateTime immutableTimestamp = Boolean.TRUE.equals(ldesFragmentEntityV1.isImmutable())
					? LocalDateTime.now()
					: null;
			LdesFragmentEntity ldesFragmentEntity = new LdesFragmentEntity(ldesFragmentEntityV1.getId(),
					ldesFragmentEntityV1.getRoot(), ldesFragmentEntityV1.getViewName(),
					ldesFragmentEntityV1.getFragmentPairs(),
					ldesFragmentEntityV1.isImmutable(), false, parentId, immutableTimestamp,
					ldesFragmentEntityV1.getMembers().size(), ldesFragmentEntityV1.getRelations());
			mongoTemplate
					.save(ldesFragmentEntity);
		});
	}

	@RollbackExecution
	public void rollback() {
		List<LdesFragmentEntity> ldesFragmentEntities = mongoTemplate.find(new Query(), LdesFragmentEntity.class);
		ldesFragmentEntities.forEach(fragmentEntity -> {
			Query query = new Query();
			query.addCriteria(Criteria.where(TREE_NODE_REFERENCES).is(fragmentEntity.getId()));
			List<String> members = mongoTemplate.find(query, LdesMemberEntity.class).stream()
					.map(LdesMemberEntity::getId).toList();
			LdesFragmentEntityV1 ldesFragmentEntityV1 = new LdesFragmentEntityV1(fragmentEntity.getId(),
					fragmentEntity.getRoot(), fragmentEntity.getViewName(),
					fragmentEntity.getFragmentPairs(),
					fragmentEntity.isImmutable(), fragmentEntity.getRelations(), members);
			mongoTemplate
					.save(ldesFragmentEntityV1);
		});
	}

	private String getParentId(List<FragmentPair> fragmentPairs, String viewName) {

		if (!fragmentPairs.isEmpty()) {
			List<FragmentPair> parentPairs = new ArrayList<>(fragmentPairs);
			parentPairs.remove(parentPairs.size() - 1);
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder
					.append("/").append(viewName);
			if (!parentPairs.isEmpty()) {

				stringBuilder.append("?");
				stringBuilder
						.append(parentPairs.stream().map(fragmentPair -> fragmentPair.fragmentKey() +
								"=" + fragmentPair.fragmentValue()).collect(Collectors.joining("&")));
			}
			return stringBuilder.toString();
		}

		return "root";
	}
}