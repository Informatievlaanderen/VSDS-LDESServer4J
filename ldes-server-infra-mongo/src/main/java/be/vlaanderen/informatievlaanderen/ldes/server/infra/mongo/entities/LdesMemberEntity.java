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

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

@Document("ldesmember")
public class LdesMemberEntity {

    @Id
    private final String id;

    private final String ldesMember;

    public LdesMemberEntity(final String id, final String ldesMember) {
        this.id = id;
        this.ldesMember = ldesMember;
    }

    public String getId() {
        return this.id;
    }

    public String getLdesMember() {
        return this.ldesMember;
    }

    public static LdesMemberEntity fromLdesMember(LdesMember ldesMember) {
        String memberId = ldesMember.getModel()
                .listStatements(null, createProperty("https://w3id.org/tree#member"), (Resource) null).nextStatement()
                .getObject().toString();

        StringWriter outputStream = new StringWriter();
        RDFDataMgr.write(outputStream, ldesMember.getModel(), RDFFormat.NQUADS);
        String ldesMemberString = outputStream.toString();

        return new LdesMemberEntity(memberId, ldesMemberString);
    }

    public LdesMember toLdesMember() {
        Model ldesMemberModel = RDFParserBuilder.create().fromString(this.ldesMember).lang(Lang.NQUADS).toModel();
        return new LdesMember(ldesMemberModel);
    }
}
