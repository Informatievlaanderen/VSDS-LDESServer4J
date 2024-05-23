package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;

import java.util.Optional;

public interface MemberRetriever {

    Optional<Member> findFirstByCollectionNameAndSequenceNrGreaterThanAndInEventSource(String collectionName, long sequenceNr);
}
