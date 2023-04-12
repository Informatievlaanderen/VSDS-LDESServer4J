package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.entity.LdesMemberEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;

import java.io.StringWriter;

public class LdesMemberEntityConverter {

	public LdesMemberEntity fromLdesMember(Member member) {
		StringWriter outputStream = new StringWriter();
		RDFDataMgr.write(outputStream, member.getModel(), Lang.NQUADS);
		String ldesMemberString = outputStream.toString();
		Long index = member.getIndex();
		return new LdesMemberEntity(member.getLdesMemberId(), member.getCollection(), index, member.getVersionOf(),
				member.getTimestamp(),
				ldesMemberString, member.getTreeNodeReferences());
	}

	public Member toLdesMember(LdesMemberEntity ldesMemberEntity) {
		Model ldesMemberModel = RDFParserBuilder.create().fromString(ldesMemberEntity.getModel()).lang(Lang.NQUADS)
				.toModel();
		return new Member(ldesMemberEntity.getId(), ldesMemberEntity.getCollection(), ldesMemberEntity.getIndex(),
				ldesMemberEntity.getVersionOf(), ldesMemberEntity.getTimestamp(), ldesMemberModel,
				ldesMemberEntity.getTreeNodeReferences());
	}
}
