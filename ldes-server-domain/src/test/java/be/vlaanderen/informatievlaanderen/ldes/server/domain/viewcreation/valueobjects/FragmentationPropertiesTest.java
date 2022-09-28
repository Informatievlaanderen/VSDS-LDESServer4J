package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingConfigurationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FragmentationPropertiesTest {

	private final FragmentationProperties fragmentationProperties = new FragmentationProperties(Map.of("key", "value"));

	@Test
	void when_ValueOfKeyExists_ThenGetReturnsValue() {
		assertEquals("value", fragmentationProperties.get("key"));
	}

	@Test
	void when_ValueOfKeyDoesNotExist_ThenMissingConfigurationExceptionIsThrown() {
		MissingConfigurationException missingConfigurationException = assertThrows(MissingConfigurationException.class,
				() -> fragmentationProperties.get("otherKey"));
		assertEquals("Configuration key otherKey is missing.", missingConfigurationException.getMessage());
	}

	@Test
	void when_ValueOfKeyExists_ThenGetOrDefaultReturnsValue() {
		assertEquals("value", fragmentationProperties.getOrDefault("key", "otherValue"));
	}

	@Test
	void when_ValueOfKeyDoesNotExist_ThenGetOrDefaultReturnsDefaultValue() {
		assertEquals("otherValue", fragmentationProperties.getOrDefault("otherKey", "otherValue"));
	}

	@Test
	void test_Equality() {
		FragmentationProperties otherFragmentationProperties = new FragmentationProperties(Map.of("key", "value"));
		assertEquals(otherFragmentationProperties, fragmentationProperties);
		assertEquals(otherFragmentationProperties.hashCode(), fragmentationProperties.hashCode());
		assertEquals(fragmentationProperties, fragmentationProperties);
		assertEquals(fragmentationProperties.hashCode(), fragmentationProperties.hashCode());
		assertEquals(otherFragmentationProperties, otherFragmentationProperties);
		assertEquals(otherFragmentationProperties.hashCode(), otherFragmentationProperties.hashCode());
	}

	@ParameterizedTest
	@ArgumentsSource(FragmentationPropertiesArgumentsProvider.class)
	void test_Inequality(Object otherFragmentationProperties) {
		assertNotEquals(fragmentationProperties, otherFragmentationProperties);
		if (otherFragmentationProperties != null)
			assertNotEquals(fragmentationProperties.hashCode(), otherFragmentationProperties.hashCode());
	}

	static class FragmentationPropertiesArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(Arguments.of(new LdesMember("some_id", null)),
					Arguments.of((Object) null));
		}
	}

}