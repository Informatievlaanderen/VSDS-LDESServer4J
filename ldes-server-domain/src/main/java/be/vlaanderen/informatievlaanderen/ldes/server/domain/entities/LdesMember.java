package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import org.apache.jena.rdf.model.*;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.RdfContants.TREE_MEMBER;
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
                .map(RDFNode::toString)
                .orElse(null);
    }

    public Statement getTreeMember() {
        return memberModel
                .listStatements(null, TREE_MEMBER, (Resource) null)
                .nextOptional()
                .orElseThrow(() -> new RuntimeException("No tree member found for ldes member %s".formatted(this)));
    }

    public String getLdesMemberId() {
        return getTreeMember().getObject().toString();
    }

    public void resetLdesMemberView(LdesConfig ldesConfig) {
        String viewCollection = String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName());

        Statement statement = getTreeMember();
        memberModel.remove(statement);
        memberModel.add(createResource(viewCollection), statement.getPredicate(), statement.getResource());
    }
}
