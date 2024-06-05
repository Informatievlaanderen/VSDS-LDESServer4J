package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("bucketisationPartitioner")
public class BucketisationPartitioner implements Partitioner {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		List<Object[]> viewNameFragmentIdPairs = entityManager.createNativeQuery("SELECT DISTINCT view_name, fragment_id FROM fragmentation_bucketisation", Object[].class).getResultList();

		Map<String, ExecutionContext> contextMap = new HashMap<>(viewNameFragmentIdPairs.size());
		for (Object[] pair : viewNameFragmentIdPairs) {
			String viewName = (String) pair[0];
			String fragmentId = (String) pair[1];

			ExecutionContext context = new ExecutionContext();
			context.putString("viewName", viewName);
			context.putString("fragmentId", fragmentId);

			contextMap.put("partition" + viewName + fragmentId, context);
		}

		return contextMap;
	}
}
