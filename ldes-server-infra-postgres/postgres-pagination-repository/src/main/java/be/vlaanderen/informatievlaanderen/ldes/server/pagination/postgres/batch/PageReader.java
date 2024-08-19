package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageMemberEntityRepository;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PageReader implements ItemReader<List<Long>> {
	private static final int PAGE_SIZE = 250;
	private final PageMemberEntityRepository repository;
	private long bucketId;

	public PageReader(PageMemberEntityRepository repository) {
		this.repository = repository;
	}

	@BeforeStep
	public void retrieveSharedData(StepExecution stepExecution) {
		bucketId = stepExecution.getExecutionContext().getLong("bucketId");
	}


	@Override
	public List<Long> read() throws ParseException, NonTransientResourceException {
		List<Long> items = repository.getUnpaginatedMembers(bucketId);
		if(items == null || items.isEmpty()){
			return null;
		}
		return items;
	}
}