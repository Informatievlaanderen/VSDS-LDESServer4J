package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.v2.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.entity.EventStreamEntity;
import jakarta.persistence.*;
import org.apache.jena.rdf.model.Model;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "dcat_datasets")
public class DcatDatasetEntity {
    @Id
    @Column(name = "collection_id", unique = true, nullable = false)
    private Integer collectionId;

    @MapsId
    @OneToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "collection_id", nullable = false)
    private EventStreamEntity eventStream;

    @Column(nullable = false, columnDefinition = "text")
    @Convert(converter = ModelConverter.class)
    private Model model;

    public DcatDatasetEntity() {}

    public DcatDatasetEntity(EventStreamEntity eventStream) {
        this.eventStream = eventStream;
    }

    public String getCollectionName() {
        return eventStream.getName();
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
