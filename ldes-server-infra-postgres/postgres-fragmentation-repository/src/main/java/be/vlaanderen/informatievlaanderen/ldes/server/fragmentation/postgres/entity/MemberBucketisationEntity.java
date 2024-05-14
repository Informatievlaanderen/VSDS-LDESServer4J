package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.MemberBucketisationEntity.BUCKETISATION;

@Document(BUCKETISATION)
public class MemberBucketisationEntity {
    public static final String BUCKETISATION = "bucketisation";
    @Id
    private final String id;
    @Indexed
    private final String viewName;
    @Indexed
    private final String fragmentId;
    private final String memberId;
    @Indexed
    private final long sequenceNr;

    public MemberBucketisationEntity(String id, String viewName, String fragmentId, String memberId, long sequenceNr) {
        this.id = id;
        this.viewName = viewName;
        this.fragmentId = fragmentId;
        this.memberId = memberId;
        this.sequenceNr = sequenceNr;
    }

    public String getId() {
        return id;
    }

    public String getViewName() {
        return viewName;
    }

    public String getFragmentId() {
        return fragmentId;
    }

    public String getMemberId() {
        return memberId;
    }

    public long getSequenceNr() {
        return sequenceNr;
    }
}
