package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Objects;

public class ViewSpecification {

	private ViewName name;
	private DcatView dcat;
	private List<Model> retentionPolicies;
	private List<FragmentationConfig> fragmentations;

	public ViewSpecification(ViewName name, List<Model> retentionPolicies,
			List<FragmentationConfig> fragmentations) {
		this.name = name;
		this.retentionPolicies = retentionPolicies;
		this.fragmentations = fragmentations;
	}

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

	public List<Model> getRetentionConfigs() {
		return retentionPolicies == null ? List.of() : retentionPolicies;
	}

	public void setRetentionPolicies(List<Model> retentionPolicies) {
		this.retentionPolicies = retentionPolicies;
	}

	public DcatView getDcat() {
		return dcat;
	}

	public void setDcat(DcatView dcat) {
		this.dcat = dcat;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ViewSpecification that))
			return false;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}