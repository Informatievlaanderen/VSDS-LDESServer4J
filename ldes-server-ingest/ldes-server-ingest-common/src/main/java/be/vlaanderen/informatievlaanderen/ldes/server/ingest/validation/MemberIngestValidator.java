package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;

public interface MemberIngestValidator {

	void validate(IngestedMember member);

}
