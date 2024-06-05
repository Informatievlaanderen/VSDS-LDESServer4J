package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.MemberPostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Component
public class MemberEntityMapper {

	public MemberEntity toMemberEntity(IngestedMember member) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		RDFWriter.source(member.getModel())
				.lang(MemberPostgresRepository.CONVERSION_LANG).output(outputStream);

		return new MemberEntity(
				member.getId(),
				member.getCollectionName(),
				member.getVersionOf(),
				member.getTimestamp(),
				member.getSequenceNr(),
				member.isInEventSource(),
				member.getTransactionId(),
				outputStream.toByteArray()
		);
	}

	public IngestedMember toMember(MemberEntity memberEntity) {
		final Model model = RDFParser.source(new ByteArrayInputStream(memberEntity.getModel()))
				.lang(MemberPostgresRepository.CONVERSION_LANG)
				.toModel();

		return new IngestedMember(
				memberEntity.getId(),
				memberEntity.getCollectionName(),
				memberEntity.getVersionOf(),
				memberEntity.getTimestamp(),
				memberEntity.getSequenceNr(),
				memberEntity.isInEventSource(),
				memberEntity.getTransactionId(),
				model
		);
	}

}
