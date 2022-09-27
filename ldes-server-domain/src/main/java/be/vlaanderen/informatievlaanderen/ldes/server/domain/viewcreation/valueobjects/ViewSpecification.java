package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import java.util.List;

public class ViewSpecification {

	private String name;
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
}
