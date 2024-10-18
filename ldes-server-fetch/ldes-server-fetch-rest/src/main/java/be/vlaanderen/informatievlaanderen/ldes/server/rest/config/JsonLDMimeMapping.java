package be.vlaanderen.informatievlaanderen.ldes.server.rest.config;

import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonLDMimeMapping implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
	@Override
	public void customize(ConfigurableServletWebServerFactory factory) {
		MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
		mappings.add("jsonld", "application/ld+json; charset=utf-8");
		factory.setMimeMappings(mappings);
	}
}