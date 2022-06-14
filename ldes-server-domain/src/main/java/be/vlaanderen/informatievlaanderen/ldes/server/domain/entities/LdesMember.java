package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFParserBuilder;

import java.nio.charset.StandardCharsets;

public class LdesMember {
    private final Model memberModel;

    public LdesMember(final Model memberModel) {
        this.memberModel = memberModel;
    }

    public LdesMember(final String ldesMember, Lang lang) {
        memberModel = RDFParserBuilder.create().fromString(ldesMember).lang(lang).toModel();
    }

    public Model getModel() {
        return memberModel;
    }
}
