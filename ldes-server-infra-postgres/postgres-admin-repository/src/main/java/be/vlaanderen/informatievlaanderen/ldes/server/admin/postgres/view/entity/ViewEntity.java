package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.ModelListConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.apache.jena.rdf.model.Model;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Table(name = "views", uniqueConstraints = @UniqueConstraint(columnNames = {"collection_id", "name"}))
public class ViewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_id", nullable = false)
    private Integer id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "collection_id", nullable = false)
    private EventStreamEntity eventStream;

    @Column(nullable = false)
    private String name;

    @Type(JsonBinaryType.class)
    @Column(name = "fragmentations", columnDefinition = "jsonb", nullable = false)
    private List<FragmentationConfigEntity> fragmentations;

    @Convert(converter = ModelListConverter.class)
    @Column(name = "retention_policies", columnDefinition = "text", nullable = false)
    private List<Model> retentionPolicies;

    @Column(name = "page_size", nullable = false)
    private Integer pageSize;

    public ViewEntity() {
    }

    public ViewEntity(String name, List<FragmentationConfigEntity> fragmentations, List<Model> retentionPolicies, Integer pageSize) {
        this.name = name;
        this.fragmentations = fragmentations;
        this.retentionPolicies = retentionPolicies;
        this.pageSize = pageSize;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EventStreamEntity getEventStream() {
        return eventStream;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public List<FragmentationConfigEntity> getFragmentations() {
        return fragmentations;
    }

    public List<Model> getRetentionPolicies() {
        return retentionPolicies;
    }

    public String getComposedViewName() {
        return eventStream.getName() + "/" + name;
    }

    public void setEventStream(EventStreamEntity eventStream) {
        this.eventStream = eventStream;
    }

    public String getName() {
        return name;
    }
}
