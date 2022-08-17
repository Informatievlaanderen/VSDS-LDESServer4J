package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.LdesFragmentConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LdesFragmentConverterImplTest {

    private static final String COLLECTION_NAME = "mobility-hindrances";
    private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
    private static final String FRAGMENT_ID = "http://localhost:8080/mobility-hindrances?generatedAtTime=" + FRAGMENTATION_VALUE_1;
    private static final String TIMESTAMP_PATH = "http://www.w3.org/ns/prov#generatedAtTime";
    public static final String DATE_TIME_TYPE ="http://www.w3.org/2001/XMLSchema#dateTime";

    private final LdesMemberRepository ldesMemberRepository = mock(LdesMemberRepository.class);

    private LdesFragmentConverterImpl ldesFragmentConverter;

    @BeforeEach
    void setUp() {
        LdesConfig ldesConfig = new LdesConfig();
        ldesConfig.setCollectionName("mobility-hindrances");
        ldesConfig.setHostName("http://localhost:8080");
        ldesConfig.setShape("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape");
        ldesConfig.setMemberType("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder");
        ldesConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
        ldesConfig.setVersionOf("http://purl.org/dc/terms/isVersionOf");
        ldesFragmentConverter = new LdesFragmentConverterImpl(ldesMemberRepository, ldesConfig);
    }


    @Test
    @DisplayName("Verify correct conversion of Empty LdesFragment")
    void when_LdesFragmentIsEmpty_ModelHasFourStatements() {
        LdesFragment ldesFragment = new LdesFragment(FRAGMENT_ID, new FragmentInfo(COLLECTION_NAME, List.of()));

        Model model = ldesFragmentConverter.toModel(ldesFragment);

        assertEquals(4, getNumerOfStatements(model));
        verifyGeneralStatements(model);
    }

    @Test
    void when_LdesFragmentExists_ModelHasGeneralStatementsAndViewStatementAndRelationStatementsAndMemberStatements() {
        Model ldesMemberModel = RDFParserBuilder.create().fromString("""
                        <http://localhost:8080/mobility-hindrances> <https://w3id.org/tree#member> <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165> .""").lang(Lang.NQUADS)
                .toModel();
        LdesMember ldesMember = new LdesMember("some_id", ldesMemberModel);
        LdesFragment ldesFragment = new LdesFragment(FRAGMENT_ID, new FragmentInfo(COLLECTION_NAME, List.of(new FragmentPair(TIMESTAMP_PATH, FRAGMENTATION_VALUE_1))));
        ldesFragment.addMember("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165");
        ldesFragment.addRelation(new TreeRelation("path", "node", "value", DATE_TIME_TYPE, "relation"));
        when(ldesMemberRepository.getLdesMembersByIds(List.of("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165"))).thenReturn(Stream.of(ldesMember));

        Model model = ldesFragmentConverter.toModel(ldesFragment);

        assertEquals(11, getNumerOfStatements(model));
        verifyGeneralStatements(model);
        verifyViewStatement(model);
        String relationObject = model.listStatements(null, TREE_RELATION, (Resource) null).nextStatement().getObject().toString();
        verifyRelationStatements(model, relationObject);
        verifyMemberStatements(model);
    }

    private void verifyRelationStatements(Model model, String relationObject) {
        assertEquals(String.format("[http://localhost:8080/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z, https://w3id.org/tree#relation, %s]", relationObject), model.listStatements(null, TREE_RELATION, (Resource) null).nextStatement().toString());
        assertEquals(String.format("[%s, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, relation]", relationObject), model.listStatements(null, RDF_SYNTAX_TYPE, (Resource) null).nextStatement().toString());
        assertEquals(String.format("[%s, https://w3id.org/tree#path, path]", relationObject), model.listStatements(null, TREE_PATH, (Resource) null).nextStatement().toString());
        assertEquals(String.format("[%s, https://w3id.org/tree#node, node]", relationObject), model.listStatements(null, TREE_NODE, (Resource) null).nextStatement().toString());
        assertEquals(String.format("[%s, https://w3id.org/tree#value, \"value\"^^http://www.w3.org/2001/XMLSchema#dateTime]", relationObject), model.listStatements(null, TREE_VALUE, (Resource) null).nextStatement().toString());
    }

    private void verifyMemberStatements(Model model) {
        assertEquals("[http://localhost:8080/mobility-hindrances, https://w3id.org/tree#member, https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165]", model.listStatements(null, TREE_MEMBER, (Resource) null).nextStatement().toString());
    }

    private void verifyViewStatement(Model model) {
        assertEquals("[http://localhost:8080/mobility-hindrances, https://w3id.org/tree#view, http://localhost:8080/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z]", model.listStatements(null, TREE_VIEW, (Resource) null).nextStatement().toString());
    }

    private int getNumerOfStatements(Model model) {
        AtomicInteger statementCounter = new AtomicInteger();
        model.listStatements().forEach((statement) -> statementCounter.getAndIncrement());
        return statementCounter.get();
    }

    private void verifyGeneralStatements(Model model) {
        assertEquals("[http://localhost:8080/mobility-hindrances, https://w3id.org/tree#shape, https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape]", model.listStatements(null, TREE_SHAPE, (Resource) null).nextStatement().toString());
        assertEquals("[http://localhost:8080/mobility-hindrances, https://w3id.org/ldes#versionOf, http://purl.org/dc/terms/isVersionOf]", model.listStatements(null, LDES_VERSION_OF, (Resource) null).nextStatement().toString());
        assertEquals("[http://localhost:8080/mobility-hindrances, https://w3id.org/ldes#timestampPath, http://www.w3.org/ns/prov#generatedAtTime]", model.listStatements(null, LDES_TIMESTAMP_PATH, (Resource) null).nextStatement().toString());
        assertEquals("[http://localhost:8080/mobility-hindrances, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, https://w3id.org/ldes#EventStream]", model.listStatements(null, RDF_SYNTAX_TYPE, createResource(LDES_EVENT_STREAM_URI)).nextStatement().toString());
    }

}