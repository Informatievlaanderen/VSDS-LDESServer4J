package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RetentionModelSerializer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.reasoner.rulesys.builtins.Regex;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ViewEntityConverterTest {

    private final String COLLECTION = "collection";
    private final String VIEW = "view";
    private final ViewName VIEWNAME = new ViewName(COLLECTION, VIEW);
    private final String RETENTION_TEXT = "<http://www.s> <http://www.p> <http://www.r> .";
    private final FragmentationConfig FRAGMENTAION_CONFIG = new FragmentationConfig();
    private final int PAGESIZE = 5;
    private Model testModel;
    private ViewSpecification view;
    private ViewEntityConverter converter;

    @BeforeEach
    void setUp() {
        testModel = RDFParser.fromString(RETENTION_TEXT).lang(Lang.NTRIPLES).toModel();
        view = new ViewSpecification(VIEWNAME, List.of(testModel), List.of(FRAGMENTAION_CONFIG), PAGESIZE);
        converter = new ViewEntityConverter(new RetentionModelSerializer(new RdfModelConverter()));
    }

    @Test
    void test_FromView() {
        ViewEntity entity = converter.fromView(view);

        assertThat(entity).hasFieldOrPropertyWithValue("viewName", VIEWNAME.asString())
                .hasFieldOrPropertyWithValue("fragmentations", List.of(FRAGMENTAION_CONFIG))
                .hasFieldOrPropertyWithValue("pageSize", PAGESIZE)
                .matches(e -> Objects.equals(
                        StringUtils.replace(e.getRetentionPolicies().getFirst(), "\n", ""),
                        RETENTION_TEXT));
    }

    @Test
    void test_ToView() {
        ViewEntity entity = new ViewEntity(VIEWNAME.asString(), List.of(RETENTION_TEXT), List.of(FRAGMENTAION_CONFIG), PAGESIZE);
        ViewSpecification viewSpecification = converter.toView(entity);

        assertThat(viewSpecification).isEqualTo(view);
    }
}