package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;

import java.util.Optional;

public interface MemberRetriever {

    Optional<FragmentationMember> findFirstByCollectionNameAndSequenceNrGreaterThanAndInEventSource(String collectionName, long sequenceNr);
}
