package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties
public class ViewConfig {

	public static final String DEFAULT_VIEW_NAME = "by-page";
	public static final String DEFAULT_VIEW_FRAGMENTATION_STRATEGY = "pagination";
	public static final Map<String, String> DEFAULT_VIEW_FRAGMENTATION_PROPERTIES = Map.of("memberLimit", "100");
	private List<ViewSpecification> views = new ArrayList<>();
	private boolean defaultView;

	public List<ViewSpecification> getViews() {
		ArrayList<ViewSpecification> viewSpecifications = new ArrayList<>(views);
		if (defaultView) {
			viewSpecifications.add(getDefaultPaginationView());
		}
		return viewSpecifications;
	}

	private ViewSpecification getDefaultPaginationView() {
		ViewSpecification viewSpecification = new ViewSpecification();
		viewSpecification.setName(DEFAULT_VIEW_NAME);
		viewSpecification.setRetentionPolicies(List.of());
		FragmentationConfig fragmentationConfig = new FragmentationConfig();
		fragmentationConfig.setName(DEFAULT_VIEW_FRAGMENTATION_STRATEGY);
		fragmentationConfig.setConfig(DEFAULT_VIEW_FRAGMENTATION_PROPERTIES);
		viewSpecification.setFragmentations(List.of(fragmentationConfig));
		return viewSpecification;
	}

	public void setViews(List<ViewSpecification> views) {
		this.views = views;
	}

	public void setDefaultView(boolean defaultView) {
		this.defaultView = defaultView;
	}
}
