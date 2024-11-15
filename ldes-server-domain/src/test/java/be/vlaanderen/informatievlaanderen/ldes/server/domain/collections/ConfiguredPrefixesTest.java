package be.vlaanderen.informatievlaanderen.ldes.server.domain.collections;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ConfiguredPrefixesTest {
	private ApplicationContextRunner runner;

	@Test
	void test_Empty() {
		new ApplicationContextRunner()
				.withBean(ConfiguredPrefixes.class)
				.run(context -> assertThat(context.getBean(Prefixes.class))
						.isNotNull()
						.extracting(Prefixes::getPrefixes, InstanceOfAssertFactories.MAP)
						.isEmpty());
	}

	@Test
	void test_WithPropertiesProvided() {
		new ApplicationContextRunner()
				.withSystemProperties("ldes-server.formatting.prefixes.myapp=http://my-app.com#", "ldes-server.formatting.prefixes.example=http://example.com/")
				.withUserConfiguration(ConfiguredPrefixes.class)
				.run(context -> assertThat(context.getBean(ConfiguredPrefixes.class))
						.isNotNull()
						.extracting(Prefixes::getPrefixes, InstanceOfAssertFactories.MAP)
						.containsAllEntriesOf(Map.of(
								"myapp", "http://my-app.com#",
								"example", "http://example.com/"
						))
						.doesNotContainKey("other-key")
						.doesNotContainKey("ldes-server.formatting.prefixes.myapp")
				);
	}
}