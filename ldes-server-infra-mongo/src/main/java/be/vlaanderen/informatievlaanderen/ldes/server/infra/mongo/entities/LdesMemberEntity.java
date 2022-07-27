package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.StringWriter;

@Document("ldesmember")
public class LdesMemberEntity {

    @Id
    private final String id;

    private final String ldesMember;

    public LdesMemberEntity(String id, final String ldesMember) {
        this.id = id;
        this.ldesMember = ldesMember;
    }

    public String getLdesMember() {
        return this.ldesMember;
    }

    public static LdesMemberEntity fromLdesMember(LdesMember ldesMember, String memberType) {
        StringWriter outputStream = new StringWriter();
        RDFDataMgr.write(outputStream, ldesMember.getModel(), Lang.NQUADS);
        String ldesMemberString = outputStream.toString();

        return new LdesMemberEntity(ldesMember.getLdesMemberId(memberType), ldesMemberString);
    }

    public LdesMember toLdesMember() {
        Model ldesMemberModel = RDFParserBuilder.create().fromString(this.ldesMember).lang(Lang.NQUADS).toModel();
        return new LdesMember(ldesMemberModel);
    }

}
