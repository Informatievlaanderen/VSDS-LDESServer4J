package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.v2.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.FragmentationConfigEntity;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "collection_id", nullable = false)
    private EventStreamEntity eventStream;

    @Column(nullable = false)
    private String name;

    @Type(JsonBinaryType.class)
    @Column(name = "fragmentations", columnDefinition = "jsonb", nullable = false)
    private List<FragmentationConfigEntity> fragmentations;

    @Type(JsonBinaryType.class)
    @Column(name = "retention_policies", columnDefinition = "jsonb", nullable = false)
    private List<String> retentionPolicies;

    @Column(name = "page_size", nullable = false)
    private Integer pageSize;

    public ViewEntity() {
    }

    public ViewEntity(String name, List<FragmentationConfigEntity> fragmentations, List<String> retentionPolicies, Integer pageSize) {
        this.name = name;
        this.fragmentations = fragmentations;
        this.retentionPolicies = retentionPolicies;
        this.pageSize = pageSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public List<FragmentationConfigEntity> getFragmentations() {
        return fragmentations;
    }

    public List<String> getRetentionPolicies() {
        return retentionPolicies;
    }

    public String getComposedViewName() {
        return eventStream.getName() + "/" + name;
    }

    public void setEventStream(EventStreamEntity eventStream) {
        this.eventStream = eventStream;
    }
}
