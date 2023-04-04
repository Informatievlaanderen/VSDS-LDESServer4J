package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import java.util.List;

public class ViewSpecification {

	private String name;
	private List<RetentionConfig> retentionPolicies;
	private List<FragmentationConfig> fragmentations;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<FragmentationConfig> getFragmentations() {
		return fragmentations;
	}

	public void setFragmentations(List<FragmentationConfig> fragmentations) {
		this.fragmentations = fragmentations;
	}

	public List<RetentionConfig> getRetentionConfigs() {
		return retentionPolicies == null ? List.of() : retentionPolicies;
	}

	public void setRetentionPolicies(List<RetentionConfig> retentionPolicies) {
		this.retentionPolicies = retentionPolicies;
	}
}
