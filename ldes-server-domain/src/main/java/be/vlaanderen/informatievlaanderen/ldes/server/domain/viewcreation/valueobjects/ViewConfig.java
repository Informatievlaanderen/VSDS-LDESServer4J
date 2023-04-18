package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notNull;

public class ViewConfig {

	public static final String DEFAULT_VIEW_NAME = "by-page";
	public static final String DEFAULT_VIEW_FRAGMENTATION_STRATEGY = "pagination";
	public static final Map<String, String> DEFAULT_VIEW_FRAGMENTATION_PROPERTIES = Map.of("memberLimit", "100",
			"bidirectionalRelations", "false");
	private final List<ViewSpecification> views;
	private final boolean defaultView;

	private ViewConfig(List<ViewSpecification> views, boolean defaultView) {
		this.views = views != null ? views : new ArrayList<>();
		this.defaultView = defaultView;
	}

	public static ViewConfig empty() {
		return new ViewConfig(new ArrayList<>(), false);
	}

	public ViewConfig withViews(List<ViewSpecification> views) {
		return new ViewConfig(views, defaultView);
	}

	public ViewConfig withDefaultView(boolean defaultView) {
		return new ViewConfig(views, defaultView);
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
		viewSpecification.setName(new ViewName(collectionName, DEFAULT_VIEW_NAME));
		viewSpecification.setRetentionPolicies(List.of());
		FragmentationConfig fragmentationConfig = new FragmentationConfig();
		fragmentationConfig.setName(DEFAULT_VIEW_FRAGMENTATION_STRATEGY);
		fragmentationConfig.setConfig(DEFAULT_VIEW_FRAGMENTATION_PROPERTIES);
		viewSpecification.setFragmentations(List.of(fragmentationConfig));
		return viewSpecification;
	}

}
