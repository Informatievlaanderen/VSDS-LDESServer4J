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
import java.time.LocalDateTime;
import java.util.List;

@Document("ldesmember")
public class LdesMemberEntity {

	@Id
	private final String id;
	@Indexed
	private final String versionOf;
	@Indexed
	private final LocalDateTime timestamp;
	private final String model;
	@Indexed
	private final List<String> treeNodeReferences;

	@Indexed
	private final String collectionName;

	public LdesMemberEntity(String id, String versionOf, LocalDateTime timestamp, final String model,
			List<String> treeNodeReferences, String collectionName) {
		this.id = id;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
		this.model = model;
		this.treeNodeReferences = treeNodeReferences;
		this.collectionName = collectionName;
	}

	public String getModel() {
		return this.model;
	}

	public static LdesMemberEntity fromLdesMember(Member member) {
		StringWriter outputStream = new StringWriter();
		RDFDataMgr.write(outputStream, member.getModel(), Lang.NQUADS);
		String ldesMemberString = outputStream.toString();
		return new LdesMemberEntity(member.getLdesMemberId(), member.getVersionOf(), member.getTimestamp(),
				ldesMemberString, member.getTreeNodeReferences(), member.getCollectionName());
	}

	public Member toLdesMember() {
		Model ldesMemberModel = RDFParserBuilder.create().fromString(this.model).lang(Lang.NQUADS).toModel();
		return new Member(this.collectionName, this.id, this.versionOf, this.timestamp, ldesMemberModel,
				this.treeNodeReferences);
	}

	public String getId() {
		return id;
	}

}
