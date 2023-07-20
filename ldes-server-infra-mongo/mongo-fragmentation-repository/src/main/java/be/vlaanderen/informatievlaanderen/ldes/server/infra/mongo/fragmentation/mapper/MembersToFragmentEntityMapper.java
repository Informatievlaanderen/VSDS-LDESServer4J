package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.MembersToFragmentEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

// TODO TVB: 20/07/23 test
@Component
public class MembersToFragmentEntityMapper {

	public MembersToFragmentEntity toMemberEntity(ViewName viewName, Model member, long sequenceNr, String memberId) {
		final StringWriter outputStream = new StringWriter();
		RDFDataMgr.write(outputStream, member, Lang.NQUADS);
		final String modelString = outputStream.toString();
		final MembersToFragmentEntity.MembersToFragmentEntityId id = new MembersToFragmentEntity.MembersToFragmentEntityId(
				viewName, sequenceNr);

		return new MembersToFragmentEntity(id, modelString, memberId);
	}

	public Member toMember(MembersToFragmentEntity entity) {
		final Model model = RDFParserBuilder.create().fromString(entity.getMemberModel()).lang(Lang.NQUADS).toModel();
		return new Member(entity.getMemberId(), model, entity.getId().sequenceNr());
	}

}
