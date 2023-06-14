package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset2.config;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.config.LdesConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties
@Configuration
public class AppConfigChangeset2 {
	private List<LdesConfig> collections;

	public AppConfigChangeset2(List<LdesConfig> collections) {
		this.collections = collections != null ? collections : new ArrayList<>();
	}

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
				.orElse(new LdesConfig());
	}

}