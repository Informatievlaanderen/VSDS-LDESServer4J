package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingStatementException;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.valueobjects.EventStreamProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class IngestedMemberHandlerTest {
	private final static String COLLECTION = "COLLECTION";
	private final static String MEMBER_ID = "http://www.example.org/member";
	private final static String TIMESTAMP_PATH = "http://www.w3.org/ns/prov#generatedAtTime";
	private final static String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";
	private final static EventStreamProperties eventStreamProperties = new EventStreamProperties(VERSION_OF_PATH,
			TIMESTAMP_PATH);
	private MemberIngestedEvent event;

	private IngestedMemberHandler ingestedMemberHandler;
	private MemberRepository memberRepository;
	private EventStreamCollection eventStreamCollection;
	@Captor
	ArgumentCaptor<Member> captor;

	@BeforeEach
	void setUp() throws URISyntaxException {
		captor = ArgumentCaptor.forClass(Member.class);
		memberRepository = mock(MemberRepository.class);
		eventStreamCollection = mock(EventStreamCollection.class);
		ingestedMemberHandler = new IngestedMemberHandler(memberRepository, eventStreamCollection);
		event = new MemberIngestedEvent(readModelFromFile("member.ttl"), MEMBER_ID, COLLECTION);
	}

	@Test
    void when_MemberIngested_Then_MemberIsSaved() {
        when(eventStreamCollection.getEventStreamProperties(COLLECTION)).thenReturn(eventStreamProperties);

        ingestedMemberHandler.handleEventMemberIngestedEvent(event);

        verify(memberRepository, times(1)).saveMember(captor.capture());
        assertEquals("version", captor.getValue().getVersionOf());
        assertEquals(LocalDateTime.parse("2022-09-28T07:14:00.000"), captor.getValue().getTimestamp());
    }

	@Test
	void when_MemberWithIncorrectPath_Then_ExceptionIsThrown() {
		EventStreamProperties differentProperties = new EventStreamProperties("differentPath", TIMESTAMP_PATH);
		when(eventStreamCollection.getEventStreamProperties(COLLECTION)).thenReturn(differentProperties);

		Exception e = assertThrows(MissingStatementException.class,
				() -> ingestedMemberHandler.handleEventMemberIngestedEvent(event));

		assertEquals("Statement could not be found. Requires: property differentPath", e.getMessage());
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

}
