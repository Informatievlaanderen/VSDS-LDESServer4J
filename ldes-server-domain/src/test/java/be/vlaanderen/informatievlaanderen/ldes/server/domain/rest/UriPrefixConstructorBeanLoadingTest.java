package be.vlaanderen.informatievlaanderen.ldes.server.domain.rest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class UriPrefixConstructorBeanLoadingTest {
	private ApplicationContextRunner applicationContextRunner;

	@BeforeEach
	void setUp() {
		applicationContextRunner = new ApplicationContextRunner()
				.withPropertyValues("ldes-server.host-name=http://localhost:8080")
				.withUserConfiguration(HostNamePrefixConstructorConfig.class)
				.withBean(RelativeUriPrefixConstructor.class);
	}

	@Test
	void test_UseRelativeUrlIsTrue() {
		applicationContextRunner
				.withPropertyValues("ldes-server.use-relative-url=true")
				.run(context -> assertThat(context)
						.hasSingleBean(RelativeUriPrefixConstructor.class)
						.doesNotHaveBean(HostNamePrefixConstructor.class)
				);
	}

	@Test
	void test_MissingUseRelativeUrl() {
		applicationContextRunner
				.run(context -> assertThat(context)
						.hasSingleBean(HostNamePrefixConstructor.class)
						.doesNotHaveBean(RelativeUriPrefixConstructor.class)
				);
	}

	@Test
	void test_UseRelativeUrlIsFalse() {
		applicationContextRunner
				.withPropertyValues("ldes-server.use-relative-url=false")
				.run(context -> assertThat(context)
						.hasSingleBean(HostNamePrefixConstructor.class)
						.doesNotHaveBean(RelativeUriPrefixConstructor.class)
				);
	}
}