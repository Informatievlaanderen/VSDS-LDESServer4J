package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageMemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.PageMemberRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PageMemberPostgresRepository implements PageMemberRepository {

    private final PageMemberEntityRepository entityRepository;

    public PageMemberPostgresRepository(PageMemberEntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    @Override
    @Transactional
    public void setPageMembersToNewPage(long newPageId, List<Long> pageIds) {
        entityRepository.setPageMembersToNewPage(newPageId, pageIds);
    }

    @Override
    @Modifying
    @Transactional
    public void deleteByViewNameAndMembersIds(ViewName viewName, List<Long> memberIds) {
        entityRepository.deleteAllByBucket_View_EventStream_NameAndBucket_View_NameAndMember_IdIn(viewName.getCollectionName(), viewName.getViewName(), memberIds);
    }
}
