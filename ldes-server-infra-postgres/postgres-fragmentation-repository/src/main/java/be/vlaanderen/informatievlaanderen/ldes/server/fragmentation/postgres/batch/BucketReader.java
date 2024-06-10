package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.MemberBucketEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.MemberBucketEntityRepository;
import org.antlr.v4.runtime.misc.NotNull;
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
	private final MemberBucketEntityMapper mapper;
	private static final int PAGE_SIZE = 100;
	private int currentPage = 0;
	private String viewName;
	private String fragmentId;

	private final MemberBucketEntityRepository repository;

	public BucketReader(MemberBucketEntityMapper mapper, MemberBucketEntityRepository repository) {
		this.mapper = mapper;
		this.repository = repository;
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		currentPage = 0;
		viewName = getViewName(stepExecution);
		fragmentId = getFragmentId(stepExecution);
	}

	@Override
	public ExitStatus afterStep(@NotNull StepExecution stepExecution) {
		return stepExecution.getExitStatus();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BucketisedMember> read() {
		Pageable pageable = PageRequest.of(currentPage++, PAGE_SIZE);
		Page<MemberBucketEntity> page = repository.findUnprocessedBuckets(viewName, fragmentId, pageable);
		if (page.isEmpty()) {
			return null;
		}

		List<BucketisedMember> out = page.getContent()
				.stream()
				.map(mapper::toBucketisedMember)
				.toList();

		return out;
	}

	private String getViewName(StepExecution stepExecution) {
		if (stepExecution.getJobParameters().getParameters().containsKey("viewName")) {
			return stepExecution.getJobParameters().getString("viewName");
		} else {
			return stepExecution.getExecutionContext().getString("viewName");
		}
	}

	private String getFragmentId(StepExecution stepExecution) {
		return stepExecution.getExecutionContext().getString("fragmentId");
	}
}
