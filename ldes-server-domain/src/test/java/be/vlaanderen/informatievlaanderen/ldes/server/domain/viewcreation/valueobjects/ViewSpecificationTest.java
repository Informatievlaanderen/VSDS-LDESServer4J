package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecificationTest.ViewSpecificationArgumentsProvider.readModelFromFile;
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
		assertEquals(viewSpecification.hashCode(),
				otherViewSpecification.hashCode());
	}

	@ParameterizedTest
	@ArgumentsSource(ViewSpecificationArgumentsProvider.class)
	void test_inequality(Object otherViewSpecification) {
		assertNotEquals(viewSpecification, otherViewSpecification);
		assertNotEquals(viewSpecification.hashCode(),
				otherViewSpecification.hashCode());
	}

	private List<FragmentationConfig> getOtherFragmentations() {
		FragmentationConfig fragmentationConfig = new FragmentationConfig();
		fragmentationConfig.setName("TimebasedFragmentation");
		fragmentationConfig.setConfig(Map.of("maxMembers", "1"));
		return List.of(fragmentationConfig);
	}

	private List<Model> getOtherRetentionPolicies() {
		Model model = readModelFromFile("retentionpolicy/versionbased/valid_versionbased.ttl");
		return List.of(model);
	}

	static class ViewSpecificationArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(
					Arguments.of(new ViewSpecification(new ViewName("otherCollection", "view"),
							getRetentionPolicies(),
							getFragmentations())),
					Arguments.of("String instead of view"));
		}

		public static List<FragmentationConfig> getFragmentations() {
			FragmentationConfig fragmentationConfig = new FragmentationConfig();
			fragmentationConfig.setName("GeospatialFragmentation");
			fragmentationConfig.setConfig(Map.of("ZoomLevel", "15"));
			return List.of(fragmentationConfig);
		}

		public static List<Model> getRetentionPolicies() {
			Model model = readModelFromFile("retentionpolicy/timebased/valid_timebased.ttl");
			return List.of(model);
		}

		static Model readModelFromFile(String fileName) {
			try {
				ClassLoader classLoader = ViewSpecificationArgumentsProvider.class.getClassLoader();
				URI uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI();

				return RDFParserBuilder.create()
						.fromString(Files.lines(Paths.get(uri)).collect(Collectors.joining())).lang(Lang.TURTLE)
						.toModel();
			} catch (Exception e) {
				throw new RuntimeException();
			}
		}

	}

}