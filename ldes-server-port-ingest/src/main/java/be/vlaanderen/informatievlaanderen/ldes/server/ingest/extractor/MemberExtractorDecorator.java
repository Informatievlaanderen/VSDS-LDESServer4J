package be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import org.apache.jena.rdf.model.Model;

import java.util.List;

public abstract class MemberExtractorDecorator implements MemberExtractor {
	private final MemberExtractor baseMemberExtractor;

	protected MemberExtractorDecorator(MemberExtractor baseMemberExtractor) {
		this.baseMemberExtractor = baseMemberExtractor;
	}

	@Override
	public List<IngestedMember> extractMembers(Model ingestedModel) {
		return baseMemberExtractor.extractMembers(ingestedModel);
	}
}
