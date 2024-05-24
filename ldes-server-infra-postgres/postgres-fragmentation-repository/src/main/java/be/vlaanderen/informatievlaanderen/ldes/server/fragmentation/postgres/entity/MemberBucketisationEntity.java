package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketisationEntity.BUCKETISATION;

@Entity
@Table(name = BUCKETISATION, indexes = {
        @Index(columnList = "viewName"),
        @Index(columnList = "fragmentId"),
        @Index(columnList = "sequenceNr")
})
public class MemberBucketisationEntity {
    public static final String BUCKETISATION = "bucketisation";
    @Id
    private String id;
    private String viewName;
    private String fragmentId;
    private String memberId;
    private long sequenceNr;

    public MemberBucketisationEntity(String id, String viewName, String fragmentId, String memberId, long sequenceNr) {
        this.id = id;
        this.viewName = viewName;
        this.fragmentId = fragmentId;
        this.memberId = memberId;
        this.sequenceNr = sequenceNr;
    }

    protected MemberBucketisationEntity() {

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
