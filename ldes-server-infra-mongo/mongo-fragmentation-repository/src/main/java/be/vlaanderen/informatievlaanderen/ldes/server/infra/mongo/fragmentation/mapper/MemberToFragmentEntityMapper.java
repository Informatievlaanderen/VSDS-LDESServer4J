package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.MemberToFragmentEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

// TODO TVB: 20/07/23 test
@Component
public class MemberToFragmentEntityMapper {

	public MemberToFragmentEntity toMemberEntity(ViewName viewName, Member member) {
		final StringWriter outputStream = new StringWriter();
		RDFDataMgr.write(outputStream, member.model(), Lang.NQUADS);
		final String modelString = outputStream.toString();
		final MemberToFragmentEntity.MembersToFragmentEntityId id =
				new MemberToFragmentEntity.MembersToFragmentEntityId(viewName, member.sequenceNr());

		return new MemberToFragmentEntity(id, modelString, member.id());
	}

	public Member toMember(MemberToFragmentEntity entity) {
		final Model model = RDFParserBuilder.create().fromString(entity.getMemberModel()).lang(Lang.NQUADS).toModel();
		return new Member(entity.getMemberId(), model, entity.getId().sequenceNr());
	}

}
