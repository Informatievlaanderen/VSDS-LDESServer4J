package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.UnpagedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRelationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageAssignment;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PartialUrl;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Component
public class PageRelationProcessor implements ItemProcessor<List<UnpagedMember>, List<PageAssignment>> {
	private final PageRepository pageRepository;
	private final PageRelationRepository pageRelationRepository;

	public PageRelationProcessor(PageRepository pageRepository, PageRelationRepository pageRelationRepository) {
		this.pageRepository = pageRepository;
		this.pageRelationRepository = pageRelationRepository;
	}

	@Override
	public List<PageAssignment> process(List<UnpagedMember> items) {
		if (items.isEmpty()) {
			return List.of();
		}

		Page pageToFill = pageRepository.getOpenPage(items.getFirst().bucketId());

		Queue<UnpagedMember> unpagedMembers = new ArrayDeque<>(items);
		List<PageAssignment> pageAssignments = new ArrayList<>();

		while (!unpagedMembers.isEmpty()) {
			pageToFill = getPageWithSpace(pageToFill);
			int membersAdded = fillPage(pageToFill, unpagedMembers.size());
			for (int i = 0; i < membersAdded; i++) {
				UnpagedMember member = unpagedMembers.poll();
				assert member != null;
				pageAssignments.add(new PageAssignment(pageToFill.getId(), pageToFill.getBucketId(), member.memberId()));
			}
		}

		return pageAssignments;
	}

	private int fillPage(Page pageToFile, int numOfMembersToAdd) {
		int membersAdded = Math.min(numOfMembersToAdd, pageToFile.getAvailableMemberSpace());
		pageToFile.incrementAssignedMemberCount(membersAdded);
		return membersAdded;
	}

	private Page getPageWithSpace(Page page) {
		if (page.isNumberLess() || page.isFull()) {
			return createNewPage(page);
		}
		return page;
	}


	private Page createNewPage(Page page) {
		final PartialUrl childPagePartialUrl = page.createChildPartialUrl();
		final long childPageId = pageRepository.createPage(page.getBucketId(), childPagePartialUrl.asString());
		closeParentIfNecessary(page);
		addRelation(page.getId(), childPageId);
		return new Page(childPageId, page.getBucketId(), childPagePartialUrl, page.getPageSize());
	}

	private void closeParentIfNecessary(Page parentPage) {
		if (!parentPage.isNumberLess()) {
			pageRepository.setPageImmutable(parentPage.getId());
		}
	}

	private void addRelation(long parentPageId, long childPageId) {
		pageRelationRepository.insertGenericBucketRelation(parentPageId, childPageId);
	}
}
