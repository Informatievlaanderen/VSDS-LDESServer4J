package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VersionCreationPropertiesTest {
	@Test
	void test_DisabledVersionCreationProperties() {
		VersionCreationProperties versionCreationProperties = VersionCreationProperties.disabled();

		assertThat(versionCreationProperties.isVersionCreationEnabled()).isFalse();
		assertThat(versionCreationProperties.getVersionDelimiter()).isNull();
	}

	@Test
	void test_OfNullableDelimiter() {
		VersionCreationProperties versionCreationProperties = VersionCreationProperties.ofNullableDelimeter(null);

		assertThat(versionCreationProperties.isVersionCreationEnabled()).isFalse();
		assertThat(versionCreationProperties.getVersionDelimiter()).isNull();
	}

	@Test
	void test_EnabledDefaultVersionCreationProperties() {
		VersionCreationProperties versionCreationProperties = VersionCreationProperties.enabledWithDefault();

		assertThat(versionCreationProperties.isVersionCreationEnabled()).isTrue();
		assertThat(versionCreationProperties.getVersionDelimiter()).isEqualTo("/");
	}

	@Test
	void test_EnabledWithCustomDelimiterVersionCreationProperties() {
		VersionCreationProperties versionCreationProperties = VersionCreationProperties.ofNullableDelimeter("&version=");

		assertThat(versionCreationProperties.isVersionCreationEnabled()).isTrue();
		assertThat(versionCreationProperties.getVersionDelimiter()).isEqualTo("&version=");
	}
}