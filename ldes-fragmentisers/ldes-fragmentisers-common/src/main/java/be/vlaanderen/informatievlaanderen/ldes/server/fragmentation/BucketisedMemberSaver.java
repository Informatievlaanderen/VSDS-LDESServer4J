package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;

public interface BucketisedMemberSaver {
    void addBucketisedMember(BucketisedMember bucketisedMember);
    void flush();
}
