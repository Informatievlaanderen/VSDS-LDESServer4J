package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.v2.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.ModelListConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.entity.EventStreamEntity;
import jakarta.persistence.*;
import org.apache.jena.rdf.model.Model;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name = "eventsources")
public class EventSourceEntity {
    @Id
    @Column(name = "collection_id", nullable = false)
    private Integer collectionId;

    @MapsId
    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "collection_id", nullable = false)
    private EventStreamEntity eventStream;

    @Convert(converter = ModelListConverter.class)
    @Column(name = "retention_policies", columnDefinition = "text", nullable = false)
    private List<Model> retentionPolicies;

    public EventSourceEntity() {
    }

    public EventSourceEntity(EventStreamEntity eventStream) {
        this.eventStream = eventStream;
    }

    public String getCollectionName() {
        return eventStream.getName();
    }

    public List<Model> getRetentionPolicies() {
        return retentionPolicies;
    }

    public void setRetentionPolicies(List<Model> retentionPolicies) {
        this.retentionPolicies = retentionPolicies;
    }
}