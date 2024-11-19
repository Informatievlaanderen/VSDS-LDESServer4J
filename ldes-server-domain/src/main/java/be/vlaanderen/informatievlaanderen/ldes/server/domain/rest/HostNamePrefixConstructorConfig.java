package be.vlaanderen.informatievlaanderen.ldes.server.domain.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.HOST_NAME_KEY;

@Configuration
public class HostNamePrefixConstructorConfig {
	@Bean
	@ConditionalOnMissingBean(UriPrefixConstructor.class)
	public UriPrefixConstructor hostNamePrefixConstructor(@Value(HOST_NAME_KEY) String hostname) {
		return new HostNamePrefixConstructor(hostname);
	}
}
