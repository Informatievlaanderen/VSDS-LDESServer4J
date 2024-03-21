package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.ViewCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class IngestedMemberHandlerTest {
	private final static String COLLECTION = "COLLECTION";
	private final static String MEMBER_ID = "http://www.example.org/member";
	private MemberIngestedEvent event;

	private IngestedMemberHandler ingestedMemberHandler;
	private MemberPropertiesRepository memberPropertiesRepository;
	private ViewCollection viewCollection;
	@Captor
	ArgumentCaptor<MemberProperties> captor;

	@BeforeEach
	void setUp() {
		captor = ArgumentCaptor.forClass(MemberProperties.class);
		memberPropertiesRepository = mock(MemberPropertiesRepository.class);
		viewCollection = mock(ViewCollection.class);
		ingestedMemberHandler = new IngestedMemberHandler(memberPropertiesRepository, viewCollection);
		event = new MemberIngestedEvent(MEMBER_ID, COLLECTION, 0L, "version", ZonedDateTime.parse("2022-09-28T07:14:00.000Z").toLocalDateTime());
	}

	@Test
	void when_MemberIngested_Then_MemberIsSaved() {
		ingestedMemberHandler.handleMemberIngestedEvent(event);

		verify(memberPropertiesRepository).insert(captor.capture());
		assertEquals("version", captor.getValue().getVersionOf());
		assertEquals(LocalDateTime.parse("2022-09-28T07:14:00.000"), captor.getValue().getTimestamp());
	}
}
