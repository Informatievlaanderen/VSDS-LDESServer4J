package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
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

	public static LdesMemberEntity fromLdesMember(Member member) {
		StringWriter outputStream = new StringWriter();
		RDFDataMgr.write(outputStream, member.getModel(), Lang.NQUADS);
		String ldesMemberString = outputStream.toString();
		return new LdesMemberEntity(member.getLdesMemberId(), ldesMemberString);
	}

	public Member toLdesMember() {
		Model ldesMemberModel = RDFParserBuilder.create().fromString(this.ldesMember).lang(Lang.NQUADS).toModel();
		return new Member(this.id, ldesMemberModel);
	}

}
