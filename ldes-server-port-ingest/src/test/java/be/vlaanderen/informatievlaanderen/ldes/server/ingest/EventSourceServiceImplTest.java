package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

	@Test
	void test_getNextMember() {
		long sequence = 0L;
		when(memberRepository.findFirstByCollectionNameAndSequenceNrGreaterThanAndInEventSource(COLLECTION_NAME, sequence))
				.thenReturn(Optional.of(createMember(0)));

		Optional<Member> result = eventSourceService.findFirstByCollectionNameAndSequenceNrGreaterThanAndInEventSource(COLLECTION_NAME, sequence);

		assertThat(result).isPresent().matches(memberOptional -> Objects.equals(memberOptional.get().getId(), "0"));
	}

	private Member createMember(int id) {
		return new Member(String.valueOf(id), COLLECTION_NAME, null, null, (long) id, true, null, null);
	}

}