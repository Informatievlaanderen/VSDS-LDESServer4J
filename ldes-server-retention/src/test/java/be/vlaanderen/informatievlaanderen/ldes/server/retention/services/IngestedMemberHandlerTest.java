package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.Collection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.mockito.Mockito.*;

class IngestedMemberHandlerTest {
    private final static String COLLECTION = "COLLECTION";
    private final static String MEMBER_ID = "http://www.example.org/member";
    private final static String TIMESTAMP_PATH = "http://www.w3.org/ns/prov#generatedAtTime";
    private final static String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";
    private final static Collection collection = new Collection(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH);
    private MemberIngestedEvent event;

    private IngestedMemberHandler ingestedMemberHandler;
    private MemberRepository memberRepository;
    private CollectionService collectionService;

    @BeforeEach
    void setUp() throws URISyntaxException {
        memberRepository = mock(MemberRepository.class);
        collectionService = mock(CollectionService.class);
        ingestedMemberHandler = new IngestedMemberHandler(memberRepository, collectionService);
        event = new MemberIngestedEvent(readModelFromFile("member.ttl"), MEMBER_ID, COLLECTION);
    }

    @Test
    void when_MemberIngested_Then_MemberIsSaved() {
        when(collectionService.getCollection(COLLECTION)).thenReturn(collection);
        Member expected = new Member(MEMBER_ID, COLLECTION, "version", LocalDateTime.parse("2022-09-28T07:14:00.000"));

        ingestedMemberHandler.handleEventMemberIngestedEvent(event);

        verify(memberRepository, times(1)).saveMember(any(Member.class));
    }

    private Model readModelFromFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
                .toString();
        return RDFDataMgr.loadModel(uri);
    }

}