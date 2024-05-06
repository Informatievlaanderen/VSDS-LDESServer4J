package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.membermapper.MemberMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.membermapper.MemberMapperCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.EventSourceService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberRetrieverImpl implements MemberRetriever {
    private final EventSourceService eventSourceService;
    private final MemberMapperCollection memberMapperCollection;

    public MemberRetrieverImpl(EventSourceService eventSourceService, MemberMapperCollection memberMapperCollection) {
        this.eventSourceService = eventSourceService;
        this.memberMapperCollection = memberMapperCollection;
    }

    @Override
    public Optional<Member> findFirstByCollectionNameAndSequenceNrGreaterThanAndInEventSource(String collectionName, long sequenceNr) {
        final MemberMapper memberMapper = memberMapperCollection.getMemberMapper(collectionName)
                .orElseThrow(() -> new MissingResourceException("eventstream", collectionName));
        return eventSourceService
                .findFirstByCollectionNameAndSequenceNrGreaterThanAndInEventSource(collectionName, sequenceNr)
                .map(memberMapper::mapToFragmentationMember);
    }
}
