package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FragmentationSpecificationTest {

	@Test
	void test_getters() {
		FragmentationSpecification fragmentationSpecification = new FragmentationSpecification("geospatial",
				new FragmentationProperties(Map.of("key", "value")));
		assertEquals("geospatial", fragmentationSpecification.getName());
		assertEquals(new FragmentationProperties(Map.of("key", "value")), fragmentationSpecification.getProperties());
	}

	@Test
	void test_EqualityOfFragmentationSpecification() {
		FragmentationSpecification fragmentationSpecification = new FragmentationSpecification("geospatial",
				new FragmentationProperties(Map.of("key", "value")));
		FragmentationSpecification otherFragmentationSpecification = new FragmentationSpecification("geospatial",
				new FragmentationProperties(Map.of("key", "value")));
		assertEquals(fragmentationSpecification, fragmentationSpecification);
		assertEquals(otherFragmentationSpecification, otherFragmentationSpecification);
		assertEquals(fragmentationSpecification, otherFragmentationSpecification);
	}

	@ParameterizedTest
	@ArgumentsSource(FragmentationSpecificationArgumentsProvider.class)
	void test_InequalityOfFragmentationSpecification(Object otherFragmentationSpecification) {
		FragmentationSpecification fragmentationSpecification = new FragmentationSpecification("geospatial",
				new FragmentationProperties(Map.of("key", "value")));
		assertNotEquals(fragmentationSpecification, otherFragmentationSpecification);
	}

	static class FragmentationSpecificationArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(Arguments.of(new LdesMember("some_id", null)),
					Arguments.of((Object) null),
					Arguments.of(new FragmentationSpecification("timebased",
							new FragmentationProperties(Map.of("key", "value")))),
					Arguments.of(new FragmentationSpecification("geospatial",
							new FragmentationProperties(Map.of("key2", "value2")))));
		}
	}

}