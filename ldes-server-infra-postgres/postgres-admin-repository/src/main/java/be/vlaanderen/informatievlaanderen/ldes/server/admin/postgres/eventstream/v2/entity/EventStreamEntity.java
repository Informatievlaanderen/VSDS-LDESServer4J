package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.entity;

import jakarta.persistence.*;

@Entity(name = "collections")
@Table(name = "collections")
public class EventStreamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_id", unique = true, nullable = false, columnDefinition = "SMALLINT")
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "timestamp_path", nullable = false)
    private String timestampPath;

    @Column(name = "version_of_path", nullable = false)
    private String versionOfPath;

    @Column(name = "create_versions", nullable = false)
    private Boolean versionCreationEnabled;

    @Column(name = "is_closed", nullable = false)
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

    public Integer getId() {
        return id;
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
