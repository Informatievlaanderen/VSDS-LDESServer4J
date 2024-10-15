package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Paginator implements Tasklet {
	private final PageMemberRepository pageMemberRepository;
	private final PageRepository pageRepository;
	private final JdbcTemplate jdbcTemplate;

	public Paginator(PageMemberRepository pageMemberRepository, PageRepository pageRepository, JdbcTemplate jdbcTemplate) {
		this.pageMemberRepository = pageMemberRepository;
		this.pageRepository = pageRepository;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		ExecutionContext context = chunkContext.getStepContext().getStepExecution().getExecutionContext();
		long bucketId = context.getLong("bucketId");

		List<Long> members = pageMemberRepository.getUnpaginatedMembersForBucket(bucketId);

		if (members.isEmpty()) {
			return RepeatStatus.FINISHED;
		}

		Page openPage = pageRepository.getOpenPage(bucketId);

		if (openPage.isNumberLess()) {
			openPage = pageRepository.createNextPage(openPage);
		}

		int membersInPage;

		for (int i = 0; i < members.size(); i += membersInPage) {
			List<Long> pageMembers = members.subList(i, Math.min(i + openPage.getAvailableMemberSpace(), members.size()));
			membersInPage = pageMembers.size();

			openPage = fillPageWithMembers(openPage, pageMembers);
		}

		updateViewStats(members.size(), Long.parseLong(chunkContext.getStepContext().getJobParameters().get("viewId").toString()));

		return RepeatStatus.FINISHED;
	}

	private Page fillPageWithMembers(Page openPage, List<Long> pageMembers) {
		openPage.incrementAssignedMemberCount(pageMembers.size());
		pageMemberRepository.assignMembersToPage(openPage, pageMembers);

		if (openPage.isFull()) {
			return pageRepository.createNextPage(openPage);
		}
		else {
			return openPage;
		}
	}

	private void updateViewStats(long uniqueMemberCount, long viewId) {
		final String SQL = """
				update view_stats vs set
					paginated_count = vs.paginated_count + ?
				where view_id = ?;
				""";

		jdbcTemplate.update(SQL, uniqueMemberCount, viewId);
	}
}
