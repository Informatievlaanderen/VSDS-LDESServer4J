package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.LdesFragmentConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.converters.LdesFragmentHttpConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class TreeViewWebConfig {

	@Bean
	public HttpMessageConverter<LdesFragment> ldesFragmentHttpConverter(
			final LdesFragmentConverter ldesFragmentConverter) {
		return new LdesFragmentHttpConverter(ldesFragmentConverter);
	}
}
