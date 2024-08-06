package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.PageMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageMemberEntityRepository;
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
}
