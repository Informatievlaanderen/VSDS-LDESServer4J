package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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
		VersionCreationProperties versionCreationProperties = VersionCreationProperties.ofNullableDelimiter(null);

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
		VersionCreationProperties versionCreationProperties = VersionCreationProperties.ofNullableDelimiter("&version=");

		assertThat(versionCreationProperties.isVersionCreationEnabled()).isTrue();
		assertThat(versionCreationProperties.getVersionDelimiter()).isEqualTo("&version=");
	}

	@Test
	void test_equality() {
		VersionCreationProperties versionCreationProperties = VersionCreationProperties.enabledWithDefault();
		VersionCreationProperties other = VersionCreationProperties.ofNullableDelimiter("/");

		assertThat(versionCreationProperties)
				.isEqualTo(versionCreationProperties)
				.hasSameHashCodeAs(other)
				.isEqualTo(other);
	}

	@ParameterizedTest
	@MethodSource
	void test_inequality(Object other) {
		VersionCreationProperties versionCreationProperties = VersionCreationProperties.enabledWithDefault();

		assertThat(versionCreationProperties)
				.isNotEqualTo(other)
				.has(differentHashCode(other));
	}

	private static Stream<Object> test_inequality() {
		return Stream.of(
				VersionCreationProperties.disabled(),
				VersionCreationProperties.ofNullableDelimiter("#"),
				"/",
				null
		);
	}

	private Condition<VersionCreationProperties> differentHashCode(Object other) {
		return new Condition<>(
				actual -> other == null || actual.hashCode() != other.hashCode(),
				"The two objects must have a different hashCode"
		);
	}
}