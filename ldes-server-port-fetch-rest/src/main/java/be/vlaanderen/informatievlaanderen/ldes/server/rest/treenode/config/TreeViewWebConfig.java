package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.converters.TreeNodeHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services.TreeNodeConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class TreeViewWebConfig {

	public static final String DEFAULT_RDF_MEDIA_TYPE = "text/turtle";

	@Bean
	public HttpMessageConverter<TreeNode> treeNodeHttpConverter(
			final TreeNodeConverter treeNodeConverter, final RdfModelConverter rdfModelConverter) {
		return new TreeNodeHttpConverter(treeNodeConverter, rdfModelConverter);
	}
}
