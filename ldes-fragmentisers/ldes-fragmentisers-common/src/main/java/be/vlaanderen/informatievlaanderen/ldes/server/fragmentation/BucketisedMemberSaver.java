package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;

import java.util.List;

public interface BucketisedMemberSaver {
    void save(List<BucketisedMember> members);
}
