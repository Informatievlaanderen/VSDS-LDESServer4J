package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository;

import java.util.List;

public interface PageMemberRepository {

    void setPageMembersToNewPage(long newPageId, List<Long> pageIds);
}
