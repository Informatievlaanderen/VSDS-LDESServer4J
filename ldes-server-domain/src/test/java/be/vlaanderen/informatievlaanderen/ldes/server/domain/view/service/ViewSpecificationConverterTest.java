package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.ModelToViewConverterException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ViewSpecificationConverterTest {
	private static final String COLLECTION_NAME = "collection";
	private static final String VIEW_NAME = "viewName";
	private ViewSpecification view;
	private ViewSpecificationConverter viewSpecificationConverter;

	@BeforeEach
	void setup() {
		AppConfig appConfig = new AppConfig();
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setHostName("http://localhost:8080");
		appConfig.setCollections(List.of(ldesConfig));
		viewSpecificationConverter = new ViewSpecificationConverter(appConfig);
		RetentionConfig retentionConfig = new RetentionConfig();
		retentionConfig.setName("retention");
		retentionConfig.setConfig(Map.of("duration", "10"));
		List<RetentionConfig> retentions = List.of(retentionConfig);
		FragmentationConfig fragmentationConfig = new FragmentationConfig();
		fragmentationConfig.setName("fragmentation");
		fragmentationConfig.setConfig(
				Map.of("pageSize", "100", "property", "example/property"));
		List<FragmentationConfig> fragmentations = List.of(fragmentationConfig);
		view = new ViewSpecification(new ViewName(COLLECTION_NAME, VIEW_NAME), retentions, fragmentations);
	}

	@Test
	void when_ValidModel_Then_ReturnViewSpecification() throws URISyntaxException {
		Model viewModel = readModelFromFile("viewconverter/view_valid.ttl");
		ViewSpecification actualView = viewSpecificationConverter.viewFromModel(viewModel, COLLECTION_NAME);
		assertEquals(view, actualView);
		assertTrue(compareList(view.getFragmentations().stream().map(FragmentationConfig::getConfig).toList(),
				actualView.getFragmentations().stream().map(FragmentationConfig::getConfig).toList()));
		assertTrue(compareList(view.getRetentionConfigs().stream().map(RetentionConfig::getConfig).toList(),
				actualView.getRetentionConfigs().stream().map(RetentionConfig::getConfig).toList()));
	}

	@Test
	void when_MissingFragmentationName_Then_ThrowException() throws URISyntaxException {
		Model viewModel = readModelFromFile("viewconverter/view_missing_fragmentation_name.ttl");
		Exception exception = assertThrows(ModelToViewConverterException.class,
				() -> viewSpecificationConverter.viewFromModel(viewModel, COLLECTION_NAME));
		assertEquals("Could not convert model to ViewSpecification:\nMissing fragmentation name",
				exception.getMessage());
	}

	@Test
	void when_MissingRetentionName_Then_ThrowException() throws URISyntaxException {
		Model viewModel = readModelFromFile("viewconverter/view_missing_retention_name.ttl");
		Exception exception = assertThrows(ModelToViewConverterException.class,
				() -> viewSpecificationConverter.viewFromModel(viewModel, COLLECTION_NAME));
		assertEquals("Could not convert model to ViewSpecification:\nMissing retention name", exception.getMessage());
	}

	@Test
	void when_ViewSpecification_Then_ReturnModel() throws URISyntaxException {
		Model viewModel = readModelFromFile("viewconverter/view_valid.ttl");
		Model actualModel = viewSpecificationConverter.modelFromView(view);
		assertTrue(viewModel.isIsomorphicWith(actualModel));
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
