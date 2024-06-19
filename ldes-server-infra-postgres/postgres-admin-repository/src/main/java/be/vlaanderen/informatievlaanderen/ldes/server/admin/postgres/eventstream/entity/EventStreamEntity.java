package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.entity.DcatDatasetEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.entity.EventSourceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.entity.ShaclShapeEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import jakarta.persistence.*;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Optional;

@Entity
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
    private Boolean closed;

    @OneToMany(mappedBy = "eventStream", fetch = FetchType.LAZY)
    private List<ViewEntity> views;

    @OneToOne(mappedBy = "eventStream")
    private DcatDatasetEntity datasetEntity;

    @OneToOne(mappedBy = "eventStream", cascade = CascadeType.PERSIST)
    private ShaclShapeEntity shaclShapeEntity;

    @OneToOne(mappedBy = "eventStream", cascade = CascadeType.PERSIST)
    private EventSourceEntity eventSourceEntity;

    public EventStreamEntity() {
    }

    public EventStreamEntity(String name, String timestampPath, String versionOfPath, Boolean versionCreationEnabled, Boolean closed) {
        this.name = name;
        this.timestampPath = timestampPath;
        this.versionOfPath = versionOfPath;
        this.versionCreationEnabled = versionCreationEnabled;
        this.closed = closed;
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

    public boolean isVersionCreationEnabled() {
        return versionCreationEnabled;
    }

    public boolean isClosed() {
        return closed;
    }

    public List<ViewEntity> getViews() {
        return views;
    }

    public Optional<Model> getDcat() {
        return Optional.ofNullable(datasetEntity).map(DcatDatasetEntity::getModel);
    }

    public ShaclShapeEntity getShaclShapeEntity() {
        return shaclShapeEntity;
    }

    public EventSourceEntity getEventSourceEntity() {
        return eventSourceEntity;
    }

    public void setViews(List<ViewEntity> views) {
        this.views = views;
    }

    public void setShaclShapeEntity(ShaclShapeEntity shaclShapeEntity) {
        this.shaclShapeEntity = shaclShapeEntity;
    }

    public void setEventSourceEntity(EventSourceEntity eventSourceEntity) {
        this.eventSourceEntity = eventSourceEntity;
    }
}
