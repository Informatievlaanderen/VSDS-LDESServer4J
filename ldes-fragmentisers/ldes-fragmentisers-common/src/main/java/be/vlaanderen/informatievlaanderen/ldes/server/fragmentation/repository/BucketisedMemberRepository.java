package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;

import java.util.List;

public interface BucketisedMemberRepository {
    void insertAll(List<BucketisedMember> members);
    List<BucketisedMember> getFirstUnallocatedMember(ViewName viewName, Long sequenceNr);
    void deleteByViewName(ViewName viewName);
    void deleteByCollection(String collectionName);
}
