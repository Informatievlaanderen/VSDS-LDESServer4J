package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.MemberBucketEntityMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BucketReader implements ItemReader<List<BucketisedMember>>, StepExecutionListener {
	private final String VIEW_NAME = "viewName";
	@PersistenceContext
	private EntityManager entityManager;
	final MemberBucketEntityMapper mapper;
	private final int pageSize = 100;
	private Long totalRecords;
	private int currentPage;
	private String viewName;
	private String fragmentId;

	public BucketReader(MemberBucketEntityMapper mapper) {
		this.mapper = mapper;
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		currentPage = 0;
		if (stepExecution.getJobParameters().getParameters().containsKey(VIEW_NAME)) {
			viewName = stepExecution.getJobParameters().getString(VIEW_NAME);
		} else {
			viewName = stepExecution.getExecutionContext().getString(VIEW_NAME);
		}

		fragmentId = stepExecution.getExecutionContext().getString("fragmentId");

		Query countQuery = entityManager.createNativeQuery("""
				select COUNT(fb.id) from fragmentation_bucketisation fb
				LEFT JOIN fetch_allocation fa ON
				    fb.view_name = CONCAT(fa.collection_name, '/', fa.view_name)
				        AND fb.member_id = fa.member_id
				WHERE fa.id IS NULL AND fb.view_name = :viewName AND fb.fragment_id = :fragmentId
						""", Long.class);
		countQuery.setParameter(VIEW_NAME, viewName);
		countQuery.setParameter("fragmentId", fragmentId);
		totalRecords = (Long) countQuery.getSingleResult();
	}

	@Override
	public ExitStatus afterStep(@NotNull StepExecution stepExecution) {
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<BucketisedMember> read() {
		if (totalRecords == null || totalRecords == 0 || currentPage >= Math.ceil(1.0 * totalRecords / pageSize)) {
			return null;
		}
		Query dataQuery = entityManager.createNativeQuery("""
				select fb.* from fragmentation_bucketisation fb
				LEFT JOIN fetch_allocation fa ON
				    fb.view_name = fa.view_name AND fb.member_id = fa.member_id
				WHERE fa.id IS NULL AND fb.view_name = :viewName AND fb.fragment_id = :fragmentId
				ORDER BY fb.id
				        """, MemberBucketEntity.class);
		dataQuery.setParameter(VIEW_NAME, viewName);
		dataQuery.setParameter("fragmentId", fragmentId);
		dataQuery.setFirstResult(currentPage * pageSize);
		dataQuery.setMaxResults(pageSize);
		List<MemberBucketEntity> listOfMyData = dataQuery.getResultList();
		currentPage++;
		return listOfMyData.stream().map(mapper::toBucketisedMember).toList();
	}
}
