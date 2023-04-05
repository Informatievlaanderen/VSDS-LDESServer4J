package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MemberTest {

	@Test
	@DisplayName("Test correct replacing of TreeMember statement")
	void when_TreeMemberStatementIsReplaced_TreeMemberStatementHasADifferentSubject() throws IOException {
		String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
				StandardCharsets.UTF_8);
		Member member = new Member(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				null, null, createModel(ldesMemberString, Lang.NQUADS), List.of());

		member.removeTreeMember();
		Statement statement = member.getModel().listStatements(null, TREE_MEMBER, (Resource) null).nextOptional()
				.orElse(null);

		assertNull(statement);
	}

	@Test
	void test_getters() {
		Member member = new Member(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464",
				LocalDateTime.of(1, 1, 1, 1, 1, 1), ModelFactory.createDefaultModel(), List.of());

		assertEquals("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				member.getLdesMemberId());
		assertEquals("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464",
				member.getVersionOf());
		assertEquals(LocalDateTime.of(1, 1, 1, 1, 1, 1),
				member.getTimestamp());
	}

	@Test
	void when_getFragmentationObjects_returnCorrespondingStatements() throws IOException {
		String ldesMemberString = FileUtils.readFileToString(
				ResourceUtils.getFile("classpath:example-ldes-member-multiple-properties-same-predicate.nq"),
				StandardCharsets.UTF_8);

		Member member = new Member(
				"http://localhost:8080/member/1",
				null, null, createModel(ldesMemberString, Lang.NQUADS), List.of());

		assertEquals(4, member.getFragmentationObjects(".*",
				"http://www.w3.org/2004/02/skos/core#prefLabel").size());
		assertEquals(1, member.getFragmentationObjects(".*/member/.*",
				"http://www.w3.org/2004/02/skos/core#prefLabel").size());
	}

	private Model createModel(final String ldesMember, final Lang lang) {
		return RDFParserBuilder.create().fromString(ldesMember).lang(lang).toModel();
	}

}