package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;

import java.util.Optional;
import java.util.stream.Stream;

public interface IngestEventSourceService {

	Stream<IngestedMember> getMemberStreamOfCollection(String collectionName);

	Optional<IngestedMember> findFirstByCollectionNameAndSequenceNrGreaterThanAndInEventSource(String collectionName, long sequenceNr);

}
