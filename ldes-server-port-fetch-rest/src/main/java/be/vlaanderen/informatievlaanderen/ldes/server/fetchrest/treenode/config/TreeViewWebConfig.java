package be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.config;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.services.TreeNodeConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.converters.TreeNodeHttpConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class TreeViewWebConfig {

	@Bean
	public HttpMessageConverter<TreeNode> treeNodeHttpConverter(
			final TreeNodeConverter treeNodeConverter) {
		return new TreeNodeHttpConverter(treeNodeConverter);
	}
}
