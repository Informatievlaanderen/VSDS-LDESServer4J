package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.FragmentationConfigExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.RetentionModelExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewSpecificationConverterTest {
	private static final String COLLECTION_NAME = "collection";
	private static final String VIEW_NAME = "viewName";
	private ViewSpecification view;
	private ViewSpecificationConverter viewSpecificationConverter;

	@BeforeEach
	void setup() throws URISyntaxException {
		viewSpecificationConverter = new ViewSpecificationConverter("http://localhost:8080", "/swagger",
				new RetentionModelExtractor(),
				new FragmentationConfigExtractor());
		Model retentionModel = readModelFromFile("viewconverter/retentionpolicies/example_timebased.ttl");
		FragmentationConfig fragmentationConfig = new FragmentationConfig();
		fragmentationConfig.setName("ExampleFragmentation");
		fragmentationConfig.setConfig(
				Map.of("pageSize", "100", "property", "example/property"));
		List<FragmentationConfig> fragmentations = List.of(fragmentationConfig);
		ViewName viewName = new ViewName(COLLECTION_NAME, VIEW_NAME);
		view = new ViewSpecification(viewName, List.of(retentionModel), fragmentations, 100);
		Model dcat = RDFParser.source("viewconverter/dcat-view-valid.ttl").lang(Lang.TURTLE).build().toModel();
		DcatView dcatView = DcatView.from(viewName, dcat);
		view.setDcat(dcatView);
	}

	@Test
	void when_ValidModel_Then_ReturnViewSpecification() throws URISyntaxException {
		Model viewModel = readModelFromFile("viewconverter/view_valid.ttl");
		ViewSpecification actualView = viewSpecificationConverter.viewFromModel(viewModel, COLLECTION_NAME);
		assertEquals(view, actualView);
		assertTrue(compareList(view.getFragmentations().stream().map(FragmentationConfig::getConfig).toList(),
				actualView.getFragmentations().stream().map(FragmentationConfig::getConfig).toList()));
		assertEquals(view.getRetentionConfigs().size(), actualView.getRetentionConfigs().size());
		for (int i = 0; i < view.getRetentionConfigs().size(); i++) {
			assertTrue(view.getRetentionConfigs().get(i).isIsomorphicWith(actualView.getRetentionConfigs().get(i)));
		}
	}

	@Test
	void when_ViewSpecification_Then_ReturnModel() throws URISyntaxException {
		Model viewModel = readModelFromFile("viewconverter/view_valid.ttl");
		Model actualModel = viewSpecificationConverter.modelFromView(view);

		assertThat(viewModel).matches(actualModel::isIsomorphicWith);
	}

	@Test
	void when_MultipleFragmentationStrategies_Then_OrderIsKept() throws URISyntaxException {
		Model expectedModel = readModelFromFile("viewconverter/view_multiple_fragmentations.ttl");
		ViewSpecification expectedViewSpecification = getExpectedViewSpecification();

		ViewSpecification actualViewSpecification = viewSpecificationConverter.viewFromModel(expectedModel,
				"mobility-hindrances");
		Model actualModel = viewSpecificationConverter.modelFromView(actualViewSpecification);

		assertEquals(expectedViewSpecification.getFragmentations(), actualViewSpecification.getFragmentations());
		assertEquals(expectedViewSpecification.getRetentionConfigs(), actualViewSpecification.getRetentionConfigs());
		assertTrue(expectedModel.isIsomorphicWith(actualModel));

	}

	private ViewSpecification getExpectedViewSpecification() {
		FragmentationConfig geospatialConfig = new FragmentationConfig();
		geospatialConfig.setName("GeospatialFragmentation");
		geospatialConfig.setConfig(
				Map.of("maxZoom", "15", "fragmentationPath", "http://www.opengis.net/ont/geosparql#asWKT"));
		FragmentationConfig paginationConfig = new FragmentationConfig();
		paginationConfig.setName("PaginationFragmentation");
		paginationConfig.setConfig(
				Map.of("memberLimit", "100"));
		List<FragmentationConfig> fragmentations = List.of(geospatialConfig, paginationConfig);
		return new ViewSpecification(new ViewName(COLLECTION_NAME, VIEW_NAME), List.of(), fragmentations, 100);
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

	private boolean compareList(List expected, List actual) {
		return expected.containsAll(actual) && actual.containsAll(expected);
	}
}
