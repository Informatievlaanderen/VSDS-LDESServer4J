package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MembersIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.ViewCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class IngestedMemberHandlerTest {
	private final static String COLLECTION = "COLLECTION";
	private final static String MEMBER_ID = "http://www.example.org/member";
    private MembersIngestedEvent event;

	private IngestedMemberHandler ingestedMemberHandler;
	private MemberPropertiesRepository memberPropertiesRepository;
    @Captor
	ArgumentCaptor<List<MemberProperties>> captor;

	@BeforeEach
	void setUp() {
		//noinspection unchecked
		captor = ArgumentCaptor.forClass(List.class);
		memberPropertiesRepository = mock(MemberPropertiesRepository.class);
        final ViewCollection viewCollection = mock(ViewCollection.class);
		ingestedMemberHandler = new IngestedMemberHandler(memberPropertiesRepository, viewCollection);
        final MembersIngestedEvent.MemberProperties memberProperties = new MembersIngestedEvent.MemberProperties(MEMBER_ID, "version", ZonedDateTime.parse("2022-09-28T07:14:00.000Z").toLocalDateTime());
		event = new MembersIngestedEvent(this, COLLECTION, List.of(memberProperties));
	}

	@Test
	void when_MemberIngested_Then_MemberIsSaved() {
		ingestedMemberHandler.handleMembersIngestedEvent(event);

		verify(memberPropertiesRepository).insertAll(captor.capture());
		assertEquals("version", captor.getValue().getFirst().getVersionOf());
		assertEquals(LocalDateTime.parse("2022-09-28T07:14:00.000"), captor.getValue().getFirst().getTimestamp());
	}
}
