package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TreeMemberTest {

    @Test
    void test() throws URISyntaxException {
        Model model = RDFParserBuilder.create().fromString("""
                <http://localhost:8080/people/1> <http://schema.org/name> "Jane Doe".""").lang(Lang.NQUADS).toModel();
        TreeMember treeMember = new TreeMember("http://localhost:8080/people/1", model);

        List<Statement> statements = treeMember.convertToStatements(model.createResource("http://localhost:8080/people"));

        Model actualModel = ModelFactory.createDefaultModel().add(statements);
        Model expectedModel = readModelFromFile("valueobjects/treemember.ttl");
        assertTrue(actualModel.isIsomorphicWith(expectedModel));
    }

    private Model readModelFromFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
        return RDFDataMgr.loadModel(uri);
    }

}