package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
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
class IngestEventSourceServiceImplTest {

	private static final String COLLECTION_NAME = "parcels";

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private IngestEventSourceServiceImpl eventSourceService;

	@Test
	void test_getMemberStreamOfCollection() {
		when(memberRepository.getMemberStreamOfCollection(COLLECTION_NAME))
				.thenReturn(Stream.of(createMember(0), createMember(1), createMember(2)));

		Stream<IngestedMember> result = eventSourceService.getMemberStreamOfCollection(COLLECTION_NAME);

		List<IngestedMember> resultList = result.toList();
		assertEquals(3, resultList.size());
	}

	private IngestedMember createMember(int id) {
		return new IngestedMember(String.valueOf(id), COLLECTION_NAME, null, null, (long) id, true, null, null);
	}

}