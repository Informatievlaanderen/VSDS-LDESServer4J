package be.vlaanderen.informatievlaanderen.ldes.server.ingest.membersequence;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngestMemberSequenceServiceTest {

	@Mock
	private MemberEntityRepository memberEntityRepository;

	@InjectMocks
	private IngestMemberSequenceService ingestMemberSequenceService;

	@Test
	void whenSequenceExists_itIsIncrementedAndReturned() {
		String existingCollection = "existing-collection";
		EventStreamCreatedEvent existingCollectionEvent =
				new EventStreamCreatedEvent(
						new EventStream(existingCollection, null, null, false)
				);
		long sequence = 20L;
		when(memberEntityRepository.findFirstByCollectionNameOrderBySequenceNrDesc(existingCollection))
				.thenReturn(Optional.of(new MemberEntity("id", existingCollection, null, null, sequence, true, null, null)));
		ingestMemberSequenceService.handleEventStreamCreated(existingCollectionEvent);

		String newCollection = "collection";
		EventStreamCreatedEvent newCollectionEvent =
				new EventStreamCreatedEvent(
						new EventStream(newCollection, null, null, false)
				);
		ingestMemberSequenceService.handleEventStreamCreated(newCollectionEvent);

		assertThat(ingestMemberSequenceService.generateNextSequence(existingCollection)).isEqualTo(sequence + 1);
		assertThat(ingestMemberSequenceService.generateNextSequence(newCollection)).isEqualTo(1);
	}

	@Test
	void whenSequenceDoesNotExist_exceptionIsThrown() {
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> ingestMemberSequenceService.generateNextSequence("fantasy"));
	}

	@Test
	void whenSequenceIsRemoved_thenItCanNoLongerBeRetrieved() {
		String collection = "collection";
		EventStreamCreatedEvent newCollectionEvent =
				new EventStreamCreatedEvent(
						new EventStream(collection, null, null, false)
				);
		ingestMemberSequenceService.handleEventStreamCreated(newCollectionEvent);

		assertThat(ingestMemberSequenceService.generateNextSequence(collection)).isEqualTo(1);

		ingestMemberSequenceService.removeSequence(collection);
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> ingestMemberSequenceService.generateNextSequence(collection));
	}
}