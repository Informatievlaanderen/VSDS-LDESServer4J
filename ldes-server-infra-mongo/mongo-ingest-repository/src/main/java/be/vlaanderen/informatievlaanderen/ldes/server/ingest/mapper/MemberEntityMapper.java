package be.vlaanderen.informatievlaanderen.ldes.server.ingest.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

// TODO: 27/04/2023 test
@Component
public class MemberEntityMapper {

    public MemberEntity toMemberEntity(Member member) {
        final StringWriter outputStream = new StringWriter();
        RDFDataMgr.write(outputStream, member.getModel(), Lang.NQUADS);
        final String modelString = outputStream.toString();
        return new MemberEntity(member.getId(), member.getCollectionName(), member.getSequenceNr(), modelString);
    }

    public Member toMember(MemberEntity memberEntity) {
        final Model model = RDFParserBuilder.create().fromString(memberEntity.getModel()).lang(Lang.NQUADS).toModel();
        return new Member(memberEntity.getId(), memberEntity.getCollectionName(), memberEntity.getSequenceNr(), model);
    }

}
