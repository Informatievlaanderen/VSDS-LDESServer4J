package be.vlaanderen.informatievlaanderen.ldes.server.ingest.skolemization;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.MemberExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.MemberExtractorDecorator;
import org.apache.jena.rdf.model.Model;

import java.util.List;

public class SkolemizedMemberExtractor extends MemberExtractorDecorator {
	public static final String SKOLEM_URI = "/.well-known/genid/";
	private final String skolemUriTemplate;

	public SkolemizedMemberExtractor(MemberExtractor baseMemberExtractor, String skolemizationDomain) {
		super(baseMemberExtractor);
		this.skolemUriTemplate = skolemizationDomain + SKOLEM_URI + "%s";
	}

	@Override
	public List<IngestedMember> extractMembers(Model ingestedModel) {
		return super.extractMembers(ingestedModel).stream()
				.map(member -> new IngestedMember(
						member.getSubject(),
						member.getCollectionName(),
						member.getVersionOf(),
						member.getTimestamp(),
						member.isInEventSource(),
						member.getTransactionId(),
						new SkolemizedModel(skolemUriTemplate, member.getModel()).getModel()
				))
				.toList();
	}
}
