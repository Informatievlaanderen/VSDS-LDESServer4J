package be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StateObjectMemberExtractorTest {
    private StateObjectMemberExtractor stateObjectMemberExtractor;

    @BeforeEach
    void setUp() {
        stateObjectMemberExtractor = new StateObjectMemberExtractor("simpsons");
    }

    @Test
    void test_memberExtraction() {
        final Model ingestedModel = RDFParser.source("bulk-members/simpsons/all.nq").lang(Lang.NQ).toModel();

        List<IngestedMember> members = stateObjectMemberExtractor.extractMembers(ingestedModel);

        List<String> txIds = members.stream().map(IngestedMember::getTransactionId).distinct().toList();
        assertThat(txIds).hasSize(1);

        List<LocalDateTime> ingestedTimestamps = members.stream().map(IngestedMember::getTimestamp).distinct().toList();
        assertThat(ingestedTimestamps).hasSize(1);

        final List<String> expectedMemberIds = Stream.of("bart", "lisa", "homer")
                .map(memberIdPart -> "simpsons/http://temporary.org#%s/%s".formatted(memberIdPart, ingestedTimestamps.getFirst().toString()))
                .toList();


        assertThat(members)
                .map(IngestedMember::getSubject)
                .containsExactlyInAnyOrderElementsOf(expectedMemberIds);

    }
}