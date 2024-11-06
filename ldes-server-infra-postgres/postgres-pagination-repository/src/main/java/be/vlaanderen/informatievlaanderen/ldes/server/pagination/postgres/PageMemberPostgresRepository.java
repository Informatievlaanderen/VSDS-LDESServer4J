package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageMemberEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PageMemberPostgresRepository implements be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageMemberRepository {

    private final PageMemberEntityRepository entityRepository;

	public PageMemberPostgresRepository(PageMemberEntityRepository entityRepository) {
        this.entityRepository = entityRepository;
	}

    @Override
    public List<Long> getUnpaginatedMembersForBucket(long bucketId) {
        return entityRepository.findByBucketIdAndPageIdIsNullOrderByMemberId(bucketId);
    }

    @Override
    public void assignMembersToPage(Page openPage, List<Long> pageMembers) {
        entityRepository.updatePageForMembers(new PageEntity(openPage.getId()), openPage.getBucketId(), pageMembers);
    }

    @Override
    public long getPaginatedMemberCountForView(long viewId, List<Long> pageMembers) {
        return entityRepository.countPaginatedMembersByViewId(viewId, pageMembers);
    }

}
