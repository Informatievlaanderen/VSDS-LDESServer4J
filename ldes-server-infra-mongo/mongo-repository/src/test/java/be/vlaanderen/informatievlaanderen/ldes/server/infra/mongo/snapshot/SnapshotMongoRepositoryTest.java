package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot.entity.SnapshotEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot.repository.SnapshotEntityRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class SnapshotMongoRepositoryTest {

	private final SnapshotEntityRepository snapshotEntityRepository = mock(SnapshotEntityRepository.class);
	private final SnapshotMongoRepository snapshotMongoRepository = new SnapshotMongoRepository(
			snapshotEntityRepository);

	@Test
	void when_SnapshotIsSaved_SnapshotEntityIsStoredInMongoCollection() {
		Snapshot snapshot = new Snapshot("id", "collectionName", "shape", LocalDateTime.now(), "snapshotOf");

		snapshotMongoRepository.saveSnapShot(snapshot);

		verify(snapshotEntityRepository, times(1)).save(any(SnapshotEntity.class));
		verifyNoMoreInteractions(snapshotEntityRepository);
	}

}