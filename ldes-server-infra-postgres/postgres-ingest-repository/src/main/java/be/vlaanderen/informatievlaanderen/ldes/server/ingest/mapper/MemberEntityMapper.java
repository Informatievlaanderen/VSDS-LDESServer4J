package be.vlaanderen.informatievlaanderen.ldes.server.ingest.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entity.MemberEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.server.ingest.MemberPostgresRepository.CONVERSION_LANG;

@Component
public class MemberEntityMapper {

    public MemberEntity toMemberEntity(Member member) {
        final String modelString = RDFWriter.source(member.getModel())
                .lang(CONVERSION_LANG)
                .asString();
        return new MemberEntity(
                member.getId(),
                member.getCollectionName(),
                member.getVersionOf(),
                member.getTimestamp(),
                member.getSequenceNr(),
                member.getTransactionId(),
                modelString
        );
    }

    public Member toMember(MemberEntity memberEntity) {
        final Model model = RDFParser.fromString(memberEntity.getModel())
                .lang(CONVERSION_LANG)
                .toModel();
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
