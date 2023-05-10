package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ViewSpecificationTest {

	private final ViewSpecification viewSpecification = new ViewSpecification(new ViewName("collection", "view"),
			ViewSpecificationArgumentsProvider.getRetentionPolicies(),
			ViewSpecificationArgumentsProvider.getFragmentations());

	@Test
	void test_equality() {
		ViewSpecification otherViewSpecification = new ViewSpecification(new ViewName("collection", "view"),
				getOtherRetentionPolicies(), getOtherFragmentations());

		assertEquals(viewSpecification, viewSpecification);
		assertEquals(otherViewSpecification, otherViewSpecification);
		assertEquals(otherViewSpecification, viewSpecification);
		assertEquals(viewSpecification.hashCode(), otherViewSpecification.hashCode());
	}

	@ParameterizedTest
	@ArgumentsSource(ViewSpecificationArgumentsProvider.class)
	void test_inequality(Object otherViewSpecification) {
		assertNotEquals(viewSpecification, otherViewSpecification);
		assertNotEquals(viewSpecification.hashCode(), otherViewSpecification.hashCode());
	}

	private List<FragmentationConfig> getOtherFragmentations() {
		FragmentationConfig fragmentationConfig = new FragmentationConfig();
		fragmentationConfig.setName("TimeBased");
		fragmentationConfig.setConfig(Map.of("maxMembers", "1"));
		return List.of(fragmentationConfig);
	}

	private List<RetentionConfig> getOtherRetentionPolicies() {
		RetentionConfig retentionConfig = new RetentionConfig();
		retentionConfig.setName("TimeBased");
		retentionConfig.setConfig(Map.of("date", "yesterday"));
		return List.of(retentionConfig);
	}

	static class ViewSpecificationArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(
					Arguments.of(new ViewSpecification(new ViewName("otherCollection", "view"), getRetentionPolicies(),
							getFragmentations())),
					Arguments.of("String instead of view"));
		}

		public static List<FragmentationConfig> getFragmentations() {
			FragmentationConfig fragmentationConfig = new FragmentationConfig();
			fragmentationConfig.setName("GeoSpatial");
			fragmentationConfig.setConfig(Map.of("ZoomLevel", "15"));
			return List.of(fragmentationConfig);
		}

		public static List<RetentionConfig> getRetentionPolicies() {
			RetentionConfig retentionConfig = new RetentionConfig();
			retentionConfig.setName("VersionBased");
			retentionConfig.setConfig(Map.of("amount", "2"));
			return List.of(retentionConfig);
		}

	}

}