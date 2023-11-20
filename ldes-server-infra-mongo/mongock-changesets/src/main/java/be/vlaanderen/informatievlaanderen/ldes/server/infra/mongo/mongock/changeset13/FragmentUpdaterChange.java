package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset13;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Set;

@ChangeUnit(id = "fragment-updater-changeset-13", order = "2023-11-20 00:00:00", author = "VSDS")
public class FragmentUpdaterChange {
	private final MongoTemplate mongoTemplate;

	private final Set<String> indicesToDelete = Set.of("root", "immutable");

	public FragmentUpdaterChange(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Execution
	public void changeSet() {
		mongoTemplate.indexOps("fragmentation_fragment").getIndexInfo().forEach(index -> {
			final String indexName = index.getName();
			if (indicesToDelete.contains(indexName)) {
				mongoTemplate.indexOps("fragmentation_fragment").dropIndex(indexName);
			}
		});
	}

	@RollbackExecution
	public void rollback() {
		// No rollback, we don't want to add the index if we fail in deleting it.
	}

}