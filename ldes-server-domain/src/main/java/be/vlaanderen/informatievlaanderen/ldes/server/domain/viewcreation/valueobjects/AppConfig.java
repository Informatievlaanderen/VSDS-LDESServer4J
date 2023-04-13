package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.CollectionNotFoundException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ConfigurationProperties
@Configuration
public class AppConfig {

	private List<LdesConfig> collections = new ArrayList<>();

	public List<LdesConfig> getCollections() {
		return collections;
	}

	public void setCollections(List<LdesConfig> collections) {
		this.collections = collections;
	}

	public LdesConfig getLdesConfig(String collectionName) {
		return getCollections()
				.stream()
				.filter(ldes -> ldes.getCollectionName().equals(collectionName))
				.findFirst()
				.orElseThrow(() -> new CollectionNotFoundException(collectionName));
	}

}
