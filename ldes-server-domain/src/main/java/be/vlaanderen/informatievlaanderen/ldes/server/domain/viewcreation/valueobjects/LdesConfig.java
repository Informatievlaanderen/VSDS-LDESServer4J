package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ConfigurationProperties
@Configuration
public class LdesConfig {

	private List<LdesSpecification> collections = new ArrayList<>();

	public List<LdesSpecification> getCollections() {
		return collections;
	}

	public void setCollections(List<LdesSpecification> collections) {
		this.collections = collections;
	}

	public Optional<LdesSpecification> getLdesSpecification(String collectionName) {
		return getCollections()
				.stream()
				.filter(ldes -> ldes.getCollectionName().equals(collectionName))
				.findFirst();
	}

}
