package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.sparql.util.ModelUtils;
import org.apache.jena.util.ResourceUtils;
import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.StringWriter;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.RdfContants.TREE_MEMBER;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

@Document("ldesmember")
public class LdesMemberEntity {

    private final String ldesMember;

    public LdesMemberEntity(final String ldesMember) {
        this.ldesMember = ldesMember;
    }

    public String getLdesMember() {
        return this.ldesMember;
    }

    public static LdesMemberEntity fromLdesMember(LdesMember ldesMember) {
        StringWriter outputStream = new StringWriter();
        RDFDataMgr.write(outputStream, ldesMember.getModel(), Lang.JSONLD11);
        String ldesMemberString = outputStream.toString();

        return new LdesMemberEntity(ldesMemberString);
    }

    public LdesMember toLdesMember() {
        Model ldesMemberModel = RDFParserBuilder.create()
                .fromString(this.ldesMember)
                .lang(Lang.JSONLD11)
                .toModel();
        return new LdesMember(ldesMemberModel);
    }
}
