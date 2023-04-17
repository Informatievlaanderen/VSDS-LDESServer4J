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

	// TODO: 17/04/2023 remove
	/**
	 * This is the full view name containing a prefixed collection name.
	 *
	 * @param name
	 */
	public void setFullViewName(String name) {
		String[] nameParts = name.split("/");
		String collectionName = nameParts[0];
		String viewName = nameParts[1];
		this.name = new ViewName(collectionName, viewName);
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
