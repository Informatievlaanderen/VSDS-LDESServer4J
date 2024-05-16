package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;

import java.util.Optional;
import java.util.stream.Stream;

public interface IngestEventSourceService {

	Stream<Member> getMemberStreamOfCollection(String collectionName);

	Optional<Member> findFirstByCollectionNameAndSequenceNrGreaterThanAndInEventSource(String collectionName, long sequenceNr);

}
