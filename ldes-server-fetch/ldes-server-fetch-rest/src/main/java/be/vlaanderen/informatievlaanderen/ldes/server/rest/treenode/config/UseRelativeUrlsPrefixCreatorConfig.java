package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.config;

import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services.TreeNodePrefixCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class UseRelativeUrlsPrefixCreatorConfig {
	@Bean
	@ConditionalOnProperty(value = "ldes-server.use-relative-url", havingValue = "true")
	public TreeNodePrefixCreator useRelativeUrlsPrefixCreator() {
		return x -> Map.of();
	}
}
