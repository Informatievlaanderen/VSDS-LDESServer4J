package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.datamodel;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.MemberPostgresRepository;
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
				member.getSubject(),
				member.getCollectionName(),
				member.getVersionOf(),
				member.getTimestamp(),
				member.getSequenceNr(),
				member.getTransactionId(),
				member.isInEventSource(),
				outputStream.toByteArray()
		);
	}

	public Member toMember(MemberEntity memberEntity) {
		final Model model = RDFParser.source(new ByteArrayInputStream(memberEntity.getModel()))
				.lang(MemberPostgresRepository.CONVERSION_LANG)
				.toModel();

		return new Member(
				memberEntity.getSubject(),
				memberEntity.getCollection().getId(),
				memberEntity.getVersionOf(),
				memberEntity.getTimestamp(),
				memberEntity.getSequenceNr(),
				memberEntity.isInEventSource(),
				memberEntity.getTransactionId(),
				model
		);
	}

}
