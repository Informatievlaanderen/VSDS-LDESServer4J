package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import org.apache.jena.rdf.model.Model;

public class LdesMember {
    private final Model memberModel;

    public LdesMember(final Model memberModel) {
        this.memberModel = memberModel;
    }

    public Model getModel() {
        return memberModel;
    }
}
