package be.vlaanderen.informatievlaanderen.ldes.server.postgres.ingest.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.postgres.ingest.MemberPostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.postgres.ingest.entity.MemberEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Component
public class MemberEntityMapper {

	public MemberEntity toMemberEntity(Member member) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		RDFWriter.source(member.getModel())
				.lang(MemberPostgresRepository.CONVERSION_LANG).output(outputStream);

		return new MemberEntity(
				member.getId(),
				member.getCollectionName(),
				member.getVersionOf(),
				member.getTimestamp(),
				member.getSequenceNr(),
				member.getTransactionId(),
				outputStream.toByteArray()
		);
	}

	public Member toMember(MemberEntity memberEntity) {
		final Model model = RDFParser.source(new ByteArrayInputStream(memberEntity.getModel()))
				.lang(MemberPostgresRepository.CONVERSION_LANG)
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
