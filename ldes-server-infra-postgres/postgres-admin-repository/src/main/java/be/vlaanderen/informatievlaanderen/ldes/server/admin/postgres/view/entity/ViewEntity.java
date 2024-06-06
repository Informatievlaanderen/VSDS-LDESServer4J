package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.service.FragmentationConfigEntityConverter;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity(name = "oldViewModel")
@Table(name = "view")
public class ViewEntity {
	@Id
	private String viewName;

	@Type(JsonBinaryType.class)
	@Column(columnDefinition = "jsonb")
	private List<String> retentionPolicies;

	@Convert(converter = FragmentationConfigEntityConverter.class)
	@Column(name = "fragmentations", columnDefinition = "text")
	private List<FragmentationConfigEntity> fragmentations;
	private int pageSize;

	protected ViewEntity() {}

	public ViewEntity(String viewName, List<String> retentionPolicies,
	                  List<FragmentationConfigEntity> fragmentations, int pageSize) {
		this.viewName = viewName;
		this.retentionPolicies = retentionPolicies;
		this.fragmentations = fragmentations;
		this.pageSize = pageSize;
	}

	public String getViewName() {
		return viewName;
	}

	public List<String> getRetentionPolicies() {
		return retentionPolicies;
	}

	public List<FragmentationConfigEntity> getFragmentations() {
		return fragmentations;
	}

	public int getPageSize() {
		return pageSize;
	}
}
