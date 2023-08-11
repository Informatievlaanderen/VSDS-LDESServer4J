package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.integrationtest;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;

public class PropertyOverrideContextInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	static final String LOCALHOST = "http://localhost:8080";

	@Override
	public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
		TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
				configurableApplicationContext, "ldes-server.host-name=" + LOCALHOST);
	}
}
