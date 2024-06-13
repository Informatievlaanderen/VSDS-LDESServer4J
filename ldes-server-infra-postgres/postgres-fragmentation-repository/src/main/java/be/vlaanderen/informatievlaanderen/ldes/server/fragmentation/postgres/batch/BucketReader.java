package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.MemberBucketEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.MemberBucketEntityRepository;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@StepScope
public class BucketReader implements ItemReader<List<BucketisedMember>>, StepExecutionListener {
	private static final String VIEW_NAME = "viewName";
	private static final int PAGE_SIZE = 100;
	private final MemberBucketEntityMapper mapper;
	private int currentPage = 0;
	private String bucketViewName;
	private String bucketFragmentId;

	private final MemberBucketEntityRepository repository;

	public BucketReader(MemberBucketEntityMapper mapper, MemberBucketEntityRepository repository) {
		this.mapper = mapper;
		this.repository = repository;
	}

	@BeforeStep
	@Override
	public void beforeStep(StepExecution stepExecution) {
		currentPage = 0;
		bucketViewName = getViewName(stepExecution);
		bucketFragmentId = getFragmentId(stepExecution);
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return stepExecution.getExitStatus();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BucketisedMember> read() {
		Pageable pageable = PageRequest.of(currentPage++, PAGE_SIZE);
		Page<MemberBucketEntity> page = repository.findUnprocessedBuckets(bucketViewName, bucketFragmentId, pageable);
		if (page.isEmpty()) {
			return null;
		}

		return page.getContent()
				.stream()
				.map(mapper::toBucketisedMember)
				.toList();
	}

	private String getViewName(StepExecution stepExecution) {
		if (stepExecution.getJobParameters().getParameters().containsKey(VIEW_NAME)) {
			return stepExecution.getJobParameters().getString(VIEW_NAME);
		} else {
			return stepExecution.getExecutionContext().getString(VIEW_NAME);
		}
	}

	private String getFragmentId(StepExecution stepExecution) {
		return stepExecution.getExecutionContext().getString("fragmentId");
	}
}
