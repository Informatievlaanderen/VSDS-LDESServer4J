package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.StringWriter;
import java.util.List;

@Document("ldesmember")
public class LdesMemberEntity {

	@Id
	private final String id;
	private final String model;
	@Indexed
	private final List<String> treeNodeReferences;

	public LdesMemberEntity(String id, final String model, List<String> treeNodeReferences) {
		this.id = id;
		this.model = model;
		this.treeNodeReferences = treeNodeReferences;
	}

	public String getModel() {
		return this.model;
	}

	public static LdesMemberEntity fromLdesMember(Member member) {
		StringWriter outputStream = new StringWriter();
		RDFDataMgr.write(outputStream, member.getModel(), Lang.NQUADS);
		String ldesMemberString = outputStream.toString();
		return new LdesMemberEntity(member.getLdesMemberId(), ldesMemberString, member.getTreeNodeReferences());
	}

	public Member toLdesMember() {
		Model ldesMemberModel = RDFParserBuilder.create().fromString(this.model).lang(Lang.NQUADS).toModel();
		return new Member(this.id, ldesMemberModel, this.treeNodeReferences);
	}

}
