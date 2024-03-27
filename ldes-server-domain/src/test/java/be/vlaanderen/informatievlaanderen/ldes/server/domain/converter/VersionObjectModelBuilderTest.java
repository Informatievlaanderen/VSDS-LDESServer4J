package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

class VersionObjectModelBuilderTest {
    private static final String MEMBER_ID = "http://example.org/measurement/1/2024-03-14T18:00:15";
    private static final String TIMESTAMP_STRING = "2024-03-14T18:00:15";
    private static final String VERSION_OF = "http://example.org/measurement/1";
    private static final String DC_TERMS = "http://purl.org/dc/terms/";

    @Test
    void given_NonEmptyModel_test_BuildVersionObjectModel() {
        final Model startingModel = RDFParser.fromString("""
                        <http://example.org/measurement/1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/netwerk#Knoop> .
                        <http://example.org/measurement/1> <https://data.vlaanderen.be/ns/netwerk#Link.geometriemiddellijn> _:Bc71573ec37565ce5dfbf75c37234fa44 .
                        _:Bc71573ec37565ce5dfbf75c37234fa44 <http://www.opengis.net/ont/geosparql#asWKT> "MULTILINESTRING ((-6.24031410164351 53.3756072010337, -6.2403628016435 53.3756159010337, -6.2404272016435 53.3756267010337, -6.24122490164335 53.3757483010336, -6.24161280164327 53.3758069010336, -6.24174840164325 53.3758198010336, -6.24215590164318 53.3758540010335, -6.24229640164315 53.3758841010335))"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .
                        """)
                .lang(Lang.NQ)
                .toModel();
        final Model actualVersionObject = VersionObjectModelBuilder.create()
                .withMemberId(MEMBER_ID)
                .withVersionOfProperties(DC_TERMS + "isVersionOf", VERSION_OF)
                .withTimestampProperties(DC_TERMS + "created", LocalDateTime.parse(TIMESTAMP_STRING))
                .withModel(startingModel)
                .buildVersionObjectModel();

        assertThat(actualVersionObject).matches(createExpectedModel()::isIsomorphicWith);
    }

    private Model createExpectedModel() {
        final String content = """
                <http://example.org/measurement/1/2024-03-14T18:00:15> <http://purl.org/dc/terms/isVersionOf> <http://example.org/measurement/1> .
                <http://example.org/measurement/1/2024-03-14T18:00:15> <http://purl.org/dc/terms/created> "%s"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
                <http://example.org/measurement/1/2024-03-14T18:00:15> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/netwerk#Knoop> .
                <http://example.org/measurement/1/2024-03-14T18:00:15> <https://data.vlaanderen.be/ns/netwerk#Link.geometriemiddellijn> _:Bc71573ec37565ce5dfbf75c37234fa44 .
                _:Bc71573ec37565ce5dfbf75c37234fa44 <http://www.opengis.net/ont/geosparql#asWKT> "MULTILINESTRING ((-6.24031410164351 53.3756072010337, -6.2403628016435 53.3756159010337, -6.2404272016435 53.3756267010337, -6.24122490164335 53.3757483010336, -6.24161280164327 53.3758069010336, -6.24174840164325 53.3758198010336, -6.24215590164318 53.3758540010335, -6.24229640164315 53.3758841010335))"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .
                    """.formatted(LocalDateTime.parse(TIMESTAMP_STRING).atZone(TimeZone.getDefault().toZoneId()).toInstant());
        return RDFParser.fromString(content).lang(Lang.NQ).toModel();
    }
}