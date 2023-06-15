package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.view;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(ViewEntityV1.COLLECTION_NAME)
public class ViewEntityV1 {

	public static final String COLLECTION_NAME = "view";

	@Id
	private final String viewName;
	private final List<String> retentionPolicies;
	private final List<FragmentationConfig> fragmentations;

	public ViewEntityV1(String viewName, List<String> retentionPolicies,
			List<FragmentationConfig> fragmentations) {
		this.viewName = viewName;
		this.retentionPolicies = retentionPolicies;
		this.fragmentations = fragmentations;
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
}
