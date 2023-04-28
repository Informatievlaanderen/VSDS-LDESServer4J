package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;

public interface MemberIngester {

	void ingest(Member member);

}
