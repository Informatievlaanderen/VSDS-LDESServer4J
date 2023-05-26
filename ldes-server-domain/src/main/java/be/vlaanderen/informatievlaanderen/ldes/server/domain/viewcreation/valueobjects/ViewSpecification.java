package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Objects;

public class ViewSpecification {

	// TODO with the new equals method this is an entity. So it needs to move.

	private ViewName name;
	private List<Model> retentionPolicies;
	private List<FragmentationConfig> fragmentations;

	public ViewSpecification(ViewName name, List<Model> retentionPolicies,
			List<FragmentationConfig> fragmentations) {
		this.name = name;
		this.retentionPolicies = retentionPolicies;
		this.fragmentations = fragmentations;
	}

	public ViewSpecification() {
		// TODO remove empty constructor so that ViewSpecification always has an
		// identity. And remove (unused) setters.
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
