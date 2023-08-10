package be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.config;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.valueobjects.TreeNodeDto;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.converters.TreeNodeHttpConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class TreeViewWebConfig {

	@Bean
	public HttpMessageConverter<TreeNodeDto> treeNodeHttpConverter() {
		return new TreeNodeHttpConverter();
	}
}
