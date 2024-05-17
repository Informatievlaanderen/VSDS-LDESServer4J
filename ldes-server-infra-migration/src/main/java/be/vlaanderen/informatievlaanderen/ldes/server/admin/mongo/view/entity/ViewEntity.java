package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "view")
public class ViewEntity {

	@Id
	private final String viewName;
	private final List<String> retentionPolicies;
	private final List<FragmentationConfig> fragmentations;
	private final int pageSize;

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

	public ViewSpecification toView() {
		List<Model> retentionModels = retentionPolicies.stream()
				.map(serializedRetentionModel -> RDFParser.fromString(serializedRetentionModel).lang(Lang.NQUADS).toModel())
				.toList();
		return new ViewSpecification(ViewName.fromString(viewName), retentionModels, fragmentations, pageSize);
	}
}
