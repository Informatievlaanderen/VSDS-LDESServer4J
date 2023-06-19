package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.Validate.notNull;

public class ViewConfig {

	private static final String DEFAULT_VIEW_NAME = "by-page";
	private static final String DEFAULT_VIEW_FRAGMENTATION_STRATEGY = "PaginationFragmentation";
	private static final Map<String, String> DEFAULT_VIEW_FRAGMENTATION_PROPERTIES = Map.of("memberLimit", "100",
			"bidirectionalRelations", "false");
	private final List<ViewSpecification> views;
	private final boolean hasDefaultView;

	private ViewConfig(List<ViewSpecification> views, boolean hasDefaultView) {
		this.views = views != null ? views : new ArrayList<>();
		this.hasDefaultView = hasDefaultView;
	}

	public static ViewConfig empty() {
		return new ViewConfig(new ArrayList<>(), false);
	}

	public ViewConfig withViews(List<ViewSpecification> views) {
		return new ViewConfig(views, hasDefaultView);
	}

	public ViewConfig withDefaultView(boolean defaultView) {
		return new ViewConfig(views, defaultView);
	}

	public List<ViewSpecification> getViews(String collectionName) {
		ArrayList<ViewSpecification> viewSpecifications = new ArrayList<>(views);
		getDefaultPaginationView(notNull(collectionName)).ifPresent(viewSpecifications::add);
		return viewSpecifications;
	}

	public Optional<ViewSpecification> getDefaultPaginationView(String collectionName) {
		if (hasDefaultView) {
			FragmentationConfig fragmentationConfig = new FragmentationConfig();
			fragmentationConfig.setName(DEFAULT_VIEW_FRAGMENTATION_STRATEGY);
			fragmentationConfig.setConfig(DEFAULT_VIEW_FRAGMENTATION_PROPERTIES);
			ViewName viewName = new ViewName(collectionName, DEFAULT_VIEW_NAME);
			ViewSpecification viewSpecification = new ViewSpecification(viewName, List.of(),
					List.of(fragmentationConfig));
			return Optional.of(viewSpecification);
		} else {
			return Optional.empty();
		}
	}

}
