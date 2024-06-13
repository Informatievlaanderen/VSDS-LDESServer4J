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

@Component("viewBucketisationPartitioner")
@JobScope
public class ViewBucketisationPartitioner implements Partitioner {

	@PersistenceContext
	private EntityManager entityManager;

	@Value("#{jobParameters}")
	private Map<String, String> jobParameters;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		String viewName = jobParameters.get("viewName");

		List<String> fragmentIds = entityManager.createNativeQuery("SELECT DISTINCT fb.fragment_id FROM fragmentation_bucketisation fb " +
		                                                           "LEFT JOIN fetch_allocation fa ON " +
		                                                           "fb.view_name = fa.view_name AND " +
		                                                           "fb.member_id = fa.member_id " +
		                                                           "WHERE fa.id IS NULL AND fb.view_name = :viewName", String.class)
				.setParameter("viewName", viewName)
				.getResultList();

		Map<String, ExecutionContext> contextMap = HashMap.newHashMap(fragmentIds.size());
		for (String fragmentId : fragmentIds) {
			ExecutionContext context = new ExecutionContext();
			context.putString("fragmentId", fragmentId);

			contextMap.put("partition: " + fragmentId.hashCode(), context);
		}

		return contextMap;
	}
}
