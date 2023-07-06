package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;

import java.util.stream.Stream;

public interface EventSourceService {

	Stream<Member> getMemberStreamOfCollection(String collectionName);

}
