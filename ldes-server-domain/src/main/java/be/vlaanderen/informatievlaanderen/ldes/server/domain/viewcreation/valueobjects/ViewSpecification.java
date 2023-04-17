package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import java.util.List;

public class ViewSpecification {

	private ViewName name;
	private List<RetentionConfig> retentionPolicies;
	private List<FragmentationConfig> fragmentations;

	public ViewName getName() {
		return name;
	}

	public void setName(ViewName name) {
		this.name = name;
	}

	public void setCollectionName(String collectionName) {
		name = name.withCollectionName(collectionName);
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
