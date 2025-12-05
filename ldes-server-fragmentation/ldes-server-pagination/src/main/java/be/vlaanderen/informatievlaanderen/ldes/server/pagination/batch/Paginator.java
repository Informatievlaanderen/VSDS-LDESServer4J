package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Paginator implements Tasklet {
	private final PageMemberRepository pageMemberRepository;
	private final PageRepository pageRepository;
    private final MemberRepository memberRepository;

	public Paginator(PageMemberRepository pageMemberRepository, PageRepository pageRepository, MemberRepository memberRepository) {
		this.pageMemberRepository = pageMemberRepository;
		this.pageRepository = pageRepository;
        this.memberRepository = memberRepository;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		ExecutionContext context = chunkContext.getStepContext().getStepExecution().getExecutionContext();
		long bucketId = context.getLong("bucketId");

		List<Long> members = pageMemberRepository.getUnpaginatedMembersForBucket(bucketId);
		chunkContext.getStepContext().getStepExecution().setReadCount(members.size());

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
            updateIsFragmented(pageMembers);
		}

		chunkContext.getStepContext().getStepExecution().setWriteCount(members.size());
		return RepeatStatus.FINISHED;
	}

	private Page fillPageWithMembers(Page openPage, List<Long> pageMembers) {
		openPage.incrementAssignedMemberCount(pageMembers.size());
		pageMemberRepository.assignMembersToPage(openPage, pageMembers);

		if (openPage.isFull()) {
			return pageRepository.createNextPage(openPage);
		} else {
			return openPage;
		}
	}

    private void updateIsFragmented(List<Long> pageMembers) {
        memberRepository.updateIsFragmented(true, pageMembers);
    }

}
