package be.vlaanderen.informatievlaanderen.ldes.server.ingest.skolemization;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.MemberExtractor;
import org.apache.jena.rdf.model.Model;

import java.util.List;

public class SkolemizedBaseMemberExtractor implements MemberExtractor {
	public static final String SKOLEM_URI = "/.well-known/genid/";
	private final MemberExtractor baseMemberExtractor;
	private final String skolemUriTemplate;

	public SkolemizedBaseMemberExtractor(MemberExtractor baseMemberExtractor, String skolemizationDomain) {
		this.baseMemberExtractor = baseMemberExtractor;
		this.skolemUriTemplate = skolemizationDomain + SKOLEM_URI + "%s";
	}

	@Override
	public List<IngestedMember> extractMembers(Model ingestedModel) {
		return baseMemberExtractor.extractMembers(ingestedModel).stream()
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
