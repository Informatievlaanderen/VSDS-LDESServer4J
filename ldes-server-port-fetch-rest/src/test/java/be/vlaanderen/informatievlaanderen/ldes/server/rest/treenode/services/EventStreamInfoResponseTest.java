package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.assertj.core.api.Assertions.assertThat;

class EventStreamInfoResponseTest {

    @Test
    void convertToStatements_IncludesMandatoryStatements() {
        String eventStreamId = "http://example.com/eventStream";
        EventStreamInfoResponse response =
                new EventStreamInfoResponse(eventStreamId, "", "", null, List.of());

        List<Statement> statements = response.convertToStatements();

        assertThat(statements).anyMatch(s ->
                s.getSubject().toString().equals(eventStreamId) &&
                        s.getPredicate().equals(RDF_SYNTAX_TYPE) &&
                        s.getObject().toString().equals(LDES_EVENT_STREAM_URI));
    }

    @Test
    void convertToStatements_AddsShapeIfPresent() {
        Model shape = RDFParser.fromString("[ a <http://www.w3.org/ns/shacl#NodeShape> ]").lang(Lang.TURTLE).toModel();

        EventStreamInfoResponse response =
                new EventStreamInfoResponse("http://example.com/eventStream", "", "", shape, List.of());

        List<Statement> statements = response.convertToStatements();

        Model model = ModelFactory.createDefaultModel().add(statements);
        Resource shapeSubject = model.listSubjectsWithProperty(RDF.type, createResource(NODE_SHAPE_TYPE)).nextResource();
        NodeIterator nodeIterator = model.listObjectsOfProperty(TREE_SHAPE);
        assertThat(nodeIterator).hasNext();
        assertThat(nodeIterator.next().asResource()).isEqualTo(shapeSubject);
    }

}