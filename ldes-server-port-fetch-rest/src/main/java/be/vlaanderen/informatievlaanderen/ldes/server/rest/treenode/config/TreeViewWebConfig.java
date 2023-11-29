package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.RequestContextExtracter;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.converters.TreeNodeHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services.TreeNodeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.USE_RELATIVE_URL_KEY;

@Configuration
public class TreeViewWebConfig {

	@Bean
	public HttpMessageConverter<TreeNode> treeNodeHttpConverter(
			final TreeNodeConverter treeNodeConverter, RequestContextExtracter RequestContextExtracter, @Value(USE_RELATIVE_URL_KEY) Boolean useRelativeUrl) {
		return new TreeNodeHttpConverter(treeNodeConverter, RequestContextExtracter, useRelativeUrl);
	}
}
