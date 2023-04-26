package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset1;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset1.entities.LdesFragmentEntityV1;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset1.entities.LdesFragmentEntityV2;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset1.entities.LdesMemberEntityV2;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset1.valueobjects.FragmentPair;
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

@ChangeUnit(id = "fragment-updater-changeset-1", order = "2", author = "VSDS")
public class FragmentUpdaterChange {
	private static final String TREE_NODE_REFERENCES = "treeNodeReferences";
	private final MongoTemplate mongoTemplate;

	public FragmentUpdaterChange(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * This is the method with the migration code
	 **/
	@Execution
	public void changeSet() {
		mongoTemplate.stream(new Query(), LdesFragmentEntityV1.class).forEach(ldesFragmentEntityV1 -> {
			String parentId = getParentId(ldesFragmentEntityV1.getFragmentPairs(), ldesFragmentEntityV1.getViewName());
			LocalDateTime immutableTimestamp = Boolean.TRUE.equals(ldesFragmentEntityV1.isImmutable())
					? LocalDateTime.now()
					: null;
			LdesFragmentEntityV2 ldesFragmentEntity = new LdesFragmentEntityV2(ldesFragmentEntityV1.getId(),
					ldesFragmentEntityV1.getRoot(), ldesFragmentEntityV1.getViewName(),
					ldesFragmentEntityV1.getFragmentPairs(),
					ldesFragmentEntityV1.isImmutable(), false, parentId, immutableTimestamp,
					ldesFragmentEntityV1.getMembers() == null ? 0 : ldesFragmentEntityV1.getMembers().size(),
					ldesFragmentEntityV1.getRelations());
			mongoTemplate.save(ldesFragmentEntity);
		});
	}

	@RollbackExecution
	public void rollback() {
		mongoTemplate.stream(new Query(), LdesFragmentEntityV2.class).forEach(fragmentEntity -> {
			Query query = new Query();
			query.addCriteria(Criteria.where(TREE_NODE_REFERENCES).is(fragmentEntity.getId()));
			List<String> members = mongoTemplate.find(query, LdesMemberEntityV2.class).stream()
					.map(LdesMemberEntityV2::getId).toList();
			LdesFragmentEntityV1 ldesFragmentEntityV1 = new LdesFragmentEntityV1(fragmentEntity.getId(),
					fragmentEntity.getRoot(), fragmentEntity.getViewName(),
					fragmentEntity.getFragmentPairs(),
					fragmentEntity.isImmutable(), fragmentEntity.getRelations(), members);
			mongoTemplate.save(ldesFragmentEntityV1);
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