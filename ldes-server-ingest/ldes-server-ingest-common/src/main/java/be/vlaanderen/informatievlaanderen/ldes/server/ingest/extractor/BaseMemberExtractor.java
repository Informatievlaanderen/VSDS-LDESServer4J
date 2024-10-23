package be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import org.apache.jena.rdf.model.Model;

import java.util.List;

public abstract class BaseMemberExtractor implements MemberExtractor {
	private final MemberExtractor memberExtractor;

	protected BaseMemberExtractor(MemberExtractor memberExtractor) {
		this.memberExtractor = memberExtractor;
	}

	@Override
	public List<IngestedMember> extractMembers(Model ingestedModel) {
		return memberExtractor.extractMembers(ingestedModel);
	}
}
