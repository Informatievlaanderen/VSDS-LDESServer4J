package be.vlaanderen.informatievlaanderen.ldes.server.ingest.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParser;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Component
public class MemberEntityMapper {

    public MemberEntity toMemberEntity(Member member) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        RDFDataMgr.write(outputStream, member.getModel(), Lang.RDFPROTO);
        final byte[] modelBytes = outputStream.toByteArray();
        return new MemberEntity(
                member.getId(),
                member.getCollectionName(),
                member.getVersionOf(),
                member.getTimestamp(),
                member.getSequenceNr(),
                member.getTransactionId(),
                modelBytes
        );
    }

    public Member toMember(MemberEntity memberEntity) {
        final Model model = RDFParser.source(new ByteArrayInputStream(memberEntity.getModel())).lang(Lang.RDFPROTO).toModel();
        return new Member(
                memberEntity.getId(),
                memberEntity.getCollectionName(),
                memberEntity.getVersionOf(),
                memberEntity.getTimestamp(),
                memberEntity.getSequenceNr(),
                memberEntity.getTransactionId(),
                model
        );
    }

}
