package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingConfigurationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ConfigPropertiesTest {

	private final ConfigProperties configProperties = new ConfigProperties(Map.of("key", "value"));

	@Test
	void when_ValueOfKeyExists_ThenGetReturnsValue() {
		assertEquals("value", configProperties.get("key"));
	}

	@Test
	void when_ValueOfKeyDoesNotExist_ThenMissingConfigurationExceptionIsThrown() {
		MissingConfigurationException missingConfigurationException = assertThrows(MissingConfigurationException.class,
				() -> configProperties.get("otherKey"));
		assertEquals("Configuration key otherKey is missing.", missingConfigurationException.getMessage());
	}

	@Test
	void when_ValueOfKeyExists_ThenGetOrDefaultReturnsValue() {
		assertEquals("value", configProperties.getOrDefault("key", "otherValue"));
	}

	@Test
	void when_ValueOfKeyDoesNotExist_ThenGetOrDefaultReturnsDefaultValue() {
		assertEquals("otherValue", configProperties.getOrDefault("otherKey", "otherValue"));
	}

	@Test
	void test_Equality() {
		ConfigProperties otherConfigProperties = new ConfigProperties(Map.of("key", "value"));
		assertEquals(otherConfigProperties, configProperties);
		assertEquals(otherConfigProperties.hashCode(), configProperties.hashCode());
		assertEquals(configProperties, configProperties);
		assertEquals(configProperties.hashCode(), configProperties.hashCode());
		assertEquals(otherConfigProperties, otherConfigProperties);
		assertEquals(otherConfigProperties.hashCode(), otherConfigProperties.hashCode());
	}

	@ParameterizedTest
	@ArgumentsSource(FragmentationPropertiesArgumentsProvider.class)
	void test_Inequality(Object otherFragmentationProperties) {
		assertNotEquals(configProperties, otherFragmentationProperties);
		if (otherFragmentationProperties != null)
			assertNotEquals(configProperties.hashCode(), otherFragmentationProperties.hashCode());
	}

	static class FragmentationPropertiesArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(Arguments.of(new Member("collectionName", "some_id", null, null, null, List.of())),
					Arguments.of((Object) null));
		}
	}

}