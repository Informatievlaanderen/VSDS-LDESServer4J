package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventSourceServiceImplTest {

	private static final String COLLECTION_NAME = "parcels";

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private EventSourceServiceImpl eventSourceService;

	@Test
    void test_getMemberStreamOfCollection() {
        when(memberRepository.getMemberStreamOfCollection(COLLECTION_NAME))
                .thenReturn(Stream.of(createMember(0), createMember(1), createMember(2)));

        Stream<Member> result = eventSourceService.getMemberStreamOfCollection(COLLECTION_NAME);

        List<Member> resultList = result.toList();
        assertEquals(3, resultList.size());
    }

	private Member createMember(int id) {
		return new Member(String.valueOf(id), COLLECTION_NAME, (long) id, null);
	}

}