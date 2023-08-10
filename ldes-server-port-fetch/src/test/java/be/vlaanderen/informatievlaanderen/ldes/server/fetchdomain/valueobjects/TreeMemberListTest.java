package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeMemberListTest {

	@Test
	void when_TreeMemberListIsConvertedToStatements_then_ResultingModelIsAsExpected() throws URISyntaxException {
		Model modelOne = RDFParserBuilder.create().fromString("""
				<http://localhost:8080/people/1> <http://schema.org/name> "Jane Doe".""").lang(Lang.NQUADS).toModel();
		TreeMember treeMemberOne = new TreeMember("http://localhost:8080/people/1", modelOne);
		Model modelTwo = RDFParserBuilder.create().fromString("""
				<http://localhost:8080/people/2> <http://schema.org/name> "John Doe".""").lang(Lang.NQUADS).toModel();
		TreeMember treeMemberTwo = new TreeMember("http://localhost:8080/people/2", modelTwo);
		TreeMemberList treeMemberList = new TreeMemberList("http://localhost:8080/people",
				List.of(treeMemberOne, treeMemberTwo));

		Model actualModel = ModelFactory.createDefaultModel().add(treeMemberList.convertToStatements());

		Model expectedModel = readModelFromFile("valueobjects/treememberlist.ttl");
		assertTrue(actualModel.isIsomorphicWith(expectedModel));
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}

}