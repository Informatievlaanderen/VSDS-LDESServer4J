package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notNull;

// TODO: 12/04/2023 remove when it is not used as bean anymore (replace with ldesconfig)
@Configuration
@ConfigurationProperties
public class ViewConfig {

	public static final String DEFAULT_VIEW_NAME = "by-page";
	public static final String DEFAULT_VIEW_FRAGMENTATION_STRATEGY = "pagination";
	public static final Map<String, String> DEFAULT_VIEW_FRAGMENTATION_PROPERTIES = Map.of("memberLimit", "100",
			"bidirectionalRelations", "false");
	private final List<ViewSpecification> views;
	private boolean defaultView;

	public ViewConfig() {
		// TODO: 12/04/2023 remove when viewconfig is not a bean anymore
		views = new ArrayList<>();
		defaultView = false;
	}

	public ViewConfig(List<ViewSpecification> views, boolean defaultView) {
		this.views = views != null ? views : new ArrayList<>();
		this.defaultView = defaultView;
	}

	public List<ViewSpecification> getViews() {
		return getViews(null);
	}

	public List<ViewSpecification> getViews(String collectionName) {
		ArrayList<ViewSpecification> viewSpecifications = new ArrayList<>(views);
		if (defaultView) {
			viewSpecifications.add(getDefaultPaginationView(notNull(collectionName)));
		}
		return viewSpecifications;
	}

	private ViewSpecification getDefaultPaginationView(String collectionName) {
		ViewSpecification viewSpecification = new ViewSpecification();
		viewSpecification.setName(DEFAULT_VIEW_NAME);
		viewSpecification.setLdesCollectionName(collectionName);
		viewSpecification.setRetentionPolicies(List.of());
		FragmentationConfig fragmentationConfig = new FragmentationConfig();
		fragmentationConfig.setName(DEFAULT_VIEW_FRAGMENTATION_STRATEGY);
		fragmentationConfig.setConfig(DEFAULT_VIEW_FRAGMENTATION_PROPERTIES);
		viewSpecification.setFragmentations(List.of(fragmentationConfig));
		return viewSpecification;
	}

	public void setDefaultView(boolean defaultView) {
		this.defaultView = defaultView;
	}
}
