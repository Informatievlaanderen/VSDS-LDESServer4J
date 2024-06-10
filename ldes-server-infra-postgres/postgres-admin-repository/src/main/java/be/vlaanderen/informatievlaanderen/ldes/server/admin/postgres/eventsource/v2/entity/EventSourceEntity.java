package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.v2.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.ModelListConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.entity.EventStreamEntity;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.apache.jena.rdf.model.Model;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Table(name = "eventsources")
public class EventSourceEntity {
    @Id
    @Column(name = "collection_id", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "collection_id", nullable = false)
    private EventStreamEntity eventStream;

    @Type(value = JsonBinaryType.class)
    @Convert(converter = ModelListConverter.class)
    @Column(name = "retention_policies", columnDefinition = "jsonb", nullable = false)
    private List<Model> retentionPolicies;

    public EventSourceEntity() {
    }

    public EventSourceEntity(EventStreamEntity eventStream, List<Model> retentionPolicies) {
        this.eventStream = eventStream;
        this.retentionPolicies = retentionPolicies;
    }

    public String getCollectionName() {
        return eventStream.getName();
    }

    public List<Model> getRetentionPolicies() {
        return retentionPolicies;
    }
}