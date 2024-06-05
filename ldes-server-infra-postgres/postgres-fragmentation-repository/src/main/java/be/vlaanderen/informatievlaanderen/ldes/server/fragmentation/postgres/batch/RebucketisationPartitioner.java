package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("rebucketisationPartitioner")
@JobScope
public class RebucketisationPartitioner implements Partitioner {

	@PersistenceContext
	private EntityManager entityManager;

	@Value("#{jobParameters}")
	private Map<String, String> jobParameters;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		String viewName = jobParameters.get("viewName");

		List<String> fragmentIds = entityManager.createNativeQuery("SELECT DISTINCT fragment_id FROM fragmentation_bucketisation " +
		                                                           "WHERE view_name = :viewName", String.class)
				.setParameter("viewName", viewName)
				.getResultList();

		Map<String, ExecutionContext> contextMap = new HashMap<>(fragmentIds.size());
		for (String fragmentId : fragmentIds) {
			ExecutionContext context = new ExecutionContext();
			context.putString("fragmentId", fragmentId);

			contextMap.put("partition" + fragmentId, context);
		}

		return contextMap;
	}
}
