package be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.EventStreamProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberTest {
    @Nested
    class GetMemberWithoutPrefix {
        @Test
        void shouldThrowException_whenIdHasNoPrefix() {
            Member member = new Member(
                    "http://localhost:8080/member/1", null,
                    "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464",
                    LocalDateTime.parse("2020-12-28T09:36:37.127"), null);

            assertThrows(IllegalStateException.class, member::getMemberIdWithoutPrefix);
        }

        @Test
        void shouldReturnIdWithoutPrefix_whenIdHasPrefix() {
            Member member = new Member(
                    "parcels/http://localhost:8080/member/1", null,
                    "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464",
                    LocalDateTime.parse("2020-12-28T09:36:37.127"), null);

            assertEquals("http://localhost:8080/member/1", member.getMemberIdWithoutPrefix());
        }
    }

    @Nested
    class GetModel {
        private static final EventStreamProperties EVENT_STREAM_PROPERTIES =
                new EventStreamProperties("http://purl.org/dc/terms/isVersionOf", "http://www.w3.org/ns/prov#generatedAtTime");
        private static final String MEMBER_MODEL_STRING = """
                <http://test-data/mobility-hindrance/1/2> <http://purl.org/dc/terms/isVersionOf> <http://test-data/mobility-hindrance/1> .
                <http://test-data/mobility-hindrance/1/2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> .
                <http://test-data/mobility-hindrance/1/2> <http://www.w3.org/ns/prov#generatedAtTime> "%s"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
                """.formatted(LocalDateTime.parse("2023-04-06T11:58:15.867").atZone(TimeZone.getDefault().toZoneId()).toInstant());

        private static final Model EXPECTED_MEMBER_MODEL = RDFParser.fromString(MEMBER_MODEL_STRING).lang(Lang.NQ).toModel();

        @Test
        void given_ModelWithEventStreamProps_when_GetModelFromMember_then_EventStreamPropsAreNotDuplicated() {
            final Model memberModel = RDFParser.fromString(MEMBER_MODEL_STRING).lang(Lang.NQ).toModel();
            final Member member = new Member(
                    "gipod/http://test-data/mobility-hindrance/1/2",
                    EVENT_STREAM_PROPERTIES,
                    "http://test-data/mobility-hindrance/1",
                    LocalDateTime.parse("2023-04-06T11:58:15.867"),
                    memberModel
            );

            assertThat(member.getModel()).matches(EXPECTED_MEMBER_MODEL::isIsomorphicWith);
        }

        @Test
        void given_ModelWithoutEventStreamProps_when_GetModelFromMember_then_EventStreamPropsAreAdded() {
            final String memberModelString = "<http://test-data/mobility-hindrance/1/2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> .";
            final Model memberModel = RDFParser.fromString(memberModelString).lang(Lang.NQ).toModel();
            final Member member = new Member(
                    "gipod/http://test-data/mobility-hindrance/1/2",
                    EVENT_STREAM_PROPERTIES,
                    "http://test-data/mobility-hindrance/1",
                    LocalDateTime.parse("2023-04-06T11:58:15.867"),
                    memberModel
            );

            assertThat(member.getModel()).matches(EXPECTED_MEMBER_MODEL::isIsomorphicWith);
        }


    }


}