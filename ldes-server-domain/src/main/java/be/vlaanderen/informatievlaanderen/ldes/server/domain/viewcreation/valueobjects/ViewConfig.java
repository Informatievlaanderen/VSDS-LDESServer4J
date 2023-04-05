package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties
public class ViewConfig {

	private List<ViewSpecification> views = new ArrayList<>();

	public List<ViewSpecification> getViews() {
		return views;
	}

	public void setViews(List<ViewSpecification> views) {
		this.views = views;
	}
}
