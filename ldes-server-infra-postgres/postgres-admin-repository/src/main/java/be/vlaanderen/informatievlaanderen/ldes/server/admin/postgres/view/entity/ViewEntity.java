package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Table(name = "view")
public class ViewEntity {
	@Id
	private String viewName;

	@Type(JsonBinaryType.class)
	@Column(columnDefinition = "jsonb")
	private List<String> retentionPolicies;
	@Type(JsonBinaryType.class)
	@Column(columnDefinition = "jsonb")
	private List<FragmentationConfig> fragmentations;
	private int pageSize;

	protected ViewEntity() {}

	public ViewEntity(String viewName, List<String> retentionPolicies,
	                  List<FragmentationConfig> fragmentations, int pageSize) {
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

	public List<FragmentationConfig> getFragmentations() {
		return fragmentations;
	}

	public int getPageSize() {
		return pageSize;
	}
}
