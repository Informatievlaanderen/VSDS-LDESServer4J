package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ConfigurationProperties
@Configuration
public class LdesConfig {

	private List<LdesSpecification> ldesStreams = new ArrayList<>();

	public List<LdesSpecification> getLdesStreams() {
		return ldesStreams;
	}

	public void setLdesStreams(List<LdesSpecification> ldesStreams) {
		this.ldesStreams = ldesStreams;
	}

	public Optional<LdesSpecification> getLdesSpecification(String collectionName) {
		return getLdesStreams()
				.stream()
				.filter(ldes -> ldes.getCollectionName().equals(collectionName))
				.findFirst();
	}

}
