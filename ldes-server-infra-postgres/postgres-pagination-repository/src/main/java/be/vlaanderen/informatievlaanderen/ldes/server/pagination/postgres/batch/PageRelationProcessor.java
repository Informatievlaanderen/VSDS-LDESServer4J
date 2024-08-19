package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRelationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageAssignment;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PartialUrl;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class PageRelationProcessor implements ItemProcessor<List<Long>, List<PageAssignment>> {
	static final String SELECT_UNPROCESSED_MEMBER_COUNT = "SELECT count(*) FROM page_members WHERE bucket_id = ? AND page_id IS NULL";
//	static final String INSERT_PAGE_RELATION_SQL = "INSERT INTO page_relations (from_page_id, to_page_id, relation_type) VALUES (?, ?, '" + RdfConstants.GENERIC_TREE_RELATION + "')";

	private final PageRepository pageRepository;
	private final PageRelationRepository pageRelationRepository;

	public PageRelationProcessor(PageRepository pageRepository, PageRelationRepository pageRelationRepository) {
		this.pageRepository = pageRepository;
		this.pageRelationRepository = pageRelationRepository;
	}

	@Override
	@Transactional
	public List<PageAssignment> process(List<Long> items) throws Exception {
		int unprocessedMemberCount = items.size();
		Page pageToFill = pageRepository.getOpenPage(items.getFirst().intValue());

		List<PageAssignment> pageAssignments = new ArrayList<>();

		while (0 < unprocessedMemberCount) {
			pageToFill = getPageWithSpace(pageToFill);
			int membersAdded = fillPage(pageToFill, unprocessedMemberCount);
			unprocessedMemberCount -= membersAdded;
			pageAssignments.add(new PageAssignment(pageToFill.getId(), pageToFill.getBucketId(), membersAdded));
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
