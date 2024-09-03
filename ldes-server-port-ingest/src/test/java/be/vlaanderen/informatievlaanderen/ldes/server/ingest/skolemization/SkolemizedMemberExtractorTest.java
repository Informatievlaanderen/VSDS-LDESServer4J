package be.vlaanderen.informatievlaanderen.ldes.server.ingest.skolemization;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.MemberExtractor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.assertj.core.api.InstanceOfAssertFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkolemizedMemberExtractorTest {
	private static final String COLLECTION = "mobility-hindrances";
	private static final String SKOLEMIZATION_DOMAIN = "http://example.com";
	private static Model model;
	@Mock
	private MemberExtractor baseMemberExtractor;

	@BeforeAll
	static void beforeAll() {
		model = RDFParser.source("skolemization/mob-hind-member.ttl").toModel();
	}

	@Test
	void test_ExtractMembers() {
		final IngestedMember ingestedMember = ingestedMember();
		when(baseMemberExtractor.extractMembers(model)).thenReturn(List.of(ingestedMember));
		final SkolemizedMemberExtractor skolemizedMemberExtractor = new SkolemizedMemberExtractor(baseMemberExtractor, SKOLEMIZATION_DOMAIN);

		final List<IngestedMember> result = skolemizedMemberExtractor.extractMembers(model);

		assertThat(result)
				.hasSize(1)
				.first()
				.extracting(IngestedMember::getModel, new InstanceOfAssertFactory<>(Model.class, SkolemizedModelAssert::new))
				.hasSkolemizedSubjectsWithPrefix(2, SKOLEMIZATION_DOMAIN + SkolemizedMemberExtractor.SKOLEM_URI)
				.hasSkolemizedObjectsWithPrefix(2, SKOLEMIZATION_DOMAIN + SkolemizedMemberExtractor.SKOLEM_URI);
	}

	private IngestedMember ingestedMember() {
		return new IngestedMember(
				"http://test-data/mobility-hindrance/1/1",
				COLLECTION,
				"http://test-data/mobility-hindrance/1",
				ZonedDateTime.parse("2023-11-30T21:45:15+01:00").toLocalDateTime(),
				true,
				UUID.randomUUID().toString(),
				model
		);
	}
}