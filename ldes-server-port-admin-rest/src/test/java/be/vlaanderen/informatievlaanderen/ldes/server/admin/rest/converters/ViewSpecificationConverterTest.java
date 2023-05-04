package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptions.ModelToViewConverterException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.RetentionConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewSpecificationConverterTest {

    private static final String COLLECTION_NAME = "collection";
    private static final String VIEW_NAME = "http://server.org/viewName";
    private ViewSpecification view;

    @BeforeEach
    void setup() {
        RetentionConfig retentionConfig = new RetentionConfig();
        retentionConfig.setName("retention");
        retentionConfig.setConfig(Map.of("http://example.org/duration", "10"));
        List<RetentionConfig> retentions = List.of(retentionConfig);
        FragmentationConfig fragmentationConfig = new FragmentationConfig();
        fragmentationConfig.setName("fragmentation");
        fragmentationConfig.setConfig(Map.of("http://example.org/pageSize", "100", "http://example.org/property", "example/property"));
        List<FragmentationConfig> fragmentations = List.of(fragmentationConfig);
        view = new ViewSpecification(new ViewName(COLLECTION_NAME, VIEW_NAME), retentions, fragmentations);
    }

    @Test
    void when_ValidModel_Then_ReturnViewSpecification() throws URISyntaxException {
        Model viewModel = readModelFromFile("viewconverter/view_valid.ttl");
        ViewSpecification actualView = ViewSpecificationConverter.viewFromModel(viewModel, COLLECTION_NAME);
        assertEquals(view, actualView);
        assertTrue(compareList(view.getFragmentations().stream().map(FragmentationConfig::getConfig).toList(), actualView.getFragmentations().stream().map(FragmentationConfig::getConfig).toList()));
        assertTrue(compareList(view.getRetentionConfigs().stream().map(RetentionConfig::getConfig).toList(), actualView.getRetentionConfigs().stream().map(RetentionConfig::getConfig).toList()));
    }

    @Test
    void when_MissingViewType_Then_ThrowException() throws URISyntaxException {
        Model viewModel = readModelFromFile("viewconverter/view_without_type.ttl");
        assertThrows(ModelToViewConverterException.class, () -> ViewSpecificationConverter.viewFromModel(viewModel, COLLECTION_NAME),
                "Could not convert model to ViewSpecification:\nMissing type: ");
    }

    @Test
    void when_ViewSpecification_Then_ReturnModel() throws URISyntaxException {
        Model viewModel = readModelFromFile("viewconverter/view_valid.ttl");
        Model actualModel = ViewSpecificationConverter.modelFromView(view);
        String v = RDFWriter.source(actualModel).lang(Lang.TURTLE).build().asString();
        String v2 = RDFWriter.source(viewModel).lang(Lang.TURTLE).build().asString();
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