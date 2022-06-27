package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.RdfContants.TREE_MEMBER;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.*;

class LdesMemberTest {

    @Test
    @DisplayName("Test correct replacing of TreeMember statement")
    void when_TreeMemberStatementIsReplaced_TreeMemberStatementHasADifferentSubject() throws IOException {
        String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"), StandardCharsets.UTF_8);
        LdesMember ldesMember = new LdesMember(createModel(ldesMemberString, Lang.NQUADS));
        Resource expectedSubject = createResource("http://some-domain.com/ldes-collection");
        Resource expectedObject = createResource("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/483");
        Property expectedPredicate = TREE_MEMBER;

        ldesMember.replaceTreeMemberStatement("http://some-domain.com","ldes-collection");
        Statement statement = ldesMember.getModel()
                .listStatements(null, expectedPredicate, (Resource) null)
                .nextOptional()
                .orElseThrow(() -> new RuntimeException("No tree member found for ldes member %s".formatted(this)));

        assertEquals(expectedSubject, statement.getSubject() );
        assertEquals(expectedObject,statement.getObject());
        assertEquals(expectedPredicate, statement.getPredicate());
    }

    private Model createModel(final String ldesMember, final Lang lang){
        return RDFParserBuilder.create().fromString(ldesMember).lang(lang).toModel();
    }

}