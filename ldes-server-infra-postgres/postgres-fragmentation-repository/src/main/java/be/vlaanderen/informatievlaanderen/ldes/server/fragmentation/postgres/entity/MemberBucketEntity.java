package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity;

import jakarta.persistence.*;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketEntity.BUCKETISATION;

@Entity
@Table(name = BUCKETISATION, indexes = {
        @Index(columnList = "viewName"),
        @Index(columnList = "fragmentId"),
        @Index(columnList = "sequenceNr")
})
public class MemberBucketEntity {
    public static final String BUCKETISATION = "fragmentation_bucketisation";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private long id;
    private String viewName;
    private String fragmentId;
    private String memberId;
    private long sequenceNr;

    public MemberBucketEntity(String viewName, String fragmentId, String memberId, long sequenceNr) {
        this.viewName = viewName;
        this.fragmentId = fragmentId;
        this.memberId = memberId;
        this.sequenceNr = sequenceNr;
    }

    protected MemberBucketEntity() {

    }

    public long getId() {
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
