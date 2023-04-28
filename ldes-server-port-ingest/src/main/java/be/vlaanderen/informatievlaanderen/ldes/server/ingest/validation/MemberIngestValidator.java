package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;

public interface MemberIngestValidator {

    void validate(Member member);

}
