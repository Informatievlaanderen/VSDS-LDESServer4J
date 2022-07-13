package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities;

import org.apache.jena.rdf.model.*;

import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.RdfConstants.TREE_MEMBER;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

public class LdesMember {

    private final Model memberModel;

    public LdesMember(final Model memberModel) {
        this.memberModel = memberModel;
    }

    public Model getModel() {
        return memberModel;
    }

    public String getFragmentationValue(String fragmentationProperty) {
        return memberModel
                .listStatements(null, ResourceFactory.createProperty(fragmentationProperty), (Resource) null)
                .nextOptional()
                .map(Statement::getObject)
                .map(RDFNode::asLiteral)
                .map(Literal::getValue)
                .map(Objects::toString)
                .orElse(null);
    }

    public String getLdesMemberId() {
        return getCurrentTreeMemberStatement().getObject().toString();
    }

    private Statement getCurrentTreeMemberStatement() {
        return memberModel
                .listStatements(null, TREE_MEMBER, (Resource) null)
                .nextOptional()
                .orElseThrow(() -> new RuntimeException("No tree member found for ldes member %s".formatted(this)));
    }

    public void replaceTreeMemberStatement(final String hostname, final String collectionName) {
        String viewCollection = String.format("%s/%s", hostname, collectionName);
        Statement currentTreeMemberStatement = getCurrentTreeMemberStatement();
        memberModel.remove(currentTreeMemberStatement);
        memberModel.add(createResource(viewCollection), currentTreeMemberStatement.getPredicate(), currentTreeMemberStatement.getResource());
    }
}
