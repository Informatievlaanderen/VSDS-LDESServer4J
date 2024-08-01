package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PageMemberRepository {
    @Transactional
    void setPageMembersToNewPage(long newPageId, List<Long> pageIds);
}
