package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

public interface BucketisedMemberRepository {
    void deleteByViewName(ViewName viewName);
    void deleteByCollection(String collectionName);
}
