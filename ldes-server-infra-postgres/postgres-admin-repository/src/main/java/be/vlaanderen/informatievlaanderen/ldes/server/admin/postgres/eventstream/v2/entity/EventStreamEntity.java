package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.entity;

import jakarta.persistence.*;

@Entity(name = "collections")
@Table(name = "collections")
public class EventStreamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_id", unique = true, nullable = false)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String timestampPath;

    @Column(nullable = false)
    private String versionOfPath;

    @Column(nullable = false)
    private Boolean versionCreationEnabled;

    @Column(nullable = false)
    private Boolean isClosed;

    public EventStreamEntity() {
    }

    public EventStreamEntity(String name, String timestampPath, String versionOfPath, Boolean versionCreationEnabled, Boolean isClosed) {
        this.name = name;
        this.timestampPath = timestampPath;
        this.versionOfPath = versionOfPath;
        this.versionCreationEnabled = versionCreationEnabled;
        this.isClosed = isClosed;
    }

    public String getName() {
        return name;
    }

    public String getTimestampPath() {
        return timestampPath;
    }

    public String getVersionOfPath() {
        return versionOfPath;
    }

    public Boolean getVersionCreationEnabled() {
        return versionCreationEnabled;
    }

    public boolean isClosed() {
        return isClosed;
    }
}
