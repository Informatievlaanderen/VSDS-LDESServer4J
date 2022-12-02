package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MemberTest {
	private final String MEMBER_TYPE = "https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder";

	@Test
	@DisplayName("Test correct replacing of TreeMember statement")
	void when_TreeMemberStatementIsReplaced_TreeMemberStatementHasADifferentSubject() throws IOException {
		String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
				StandardCharsets.UTF_8);
		Member member = new Member(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				createModel(ldesMemberString, Lang.NQUADS), treeNodeReferences);

		member.removeTreeMember();
		Statement statement = member.getModel().listStatements(null, TREE_MEMBER, (Resource) null).nextOptional()
				.orElse(null);

		assertNull(statement);
	}

	@Test
	@DisplayName("Verify retrieving of member id from LdesMember")
	void when_TreeMemberStatementIsAvailableInModel_LdesMemberId() throws IOException {
		String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
				StandardCharsets.UTF_8);
		Member member = new Member(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				createModel(ldesMemberString, Lang.NQUADS), treeNodeReferences);
		assertEquals("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				member.getLdesMemberId());
	}

	@Test
	void when_getFragmentationObjects_returnCorrespondingStatements() throws IOException {
		String ldesMemberString = FileUtils.readFileToString(
				ResourceUtils.getFile("classpath:example-ldes-member-multiple-properties-same-predicate.nq"),
				StandardCharsets.UTF_8);

		Member member = new Member(
				"http://localhost:8080/member/1",
				createModel(ldesMemberString, Lang.NQUADS), treeNodeReferences);

		assertEquals(4, member.getFragmentationObjects(
				"PREFIX core: <http://www.w3.org/2004/02/skos/core#> SELECT ?x WHERE { ?s core:prefLabel ?x . }")
				.size());
	}

	private Model createModel(final String ldesMember, final Lang lang) {
		return RDFParserBuilder.create().fromString(ldesMember).lang(lang).toModel();
	}

}