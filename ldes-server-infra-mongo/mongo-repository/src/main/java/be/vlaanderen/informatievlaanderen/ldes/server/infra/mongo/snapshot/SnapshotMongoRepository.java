package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository.SnapshotRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot.entity.SnapshotEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot.repository.SnapshotEntityRepository;

public class SnapshotMongoRepository implements SnapshotRepository {
	private final SnapshotEntityRepository snapshotEntityRepository;

	public SnapshotMongoRepository(SnapshotEntityRepository snapshotEntityRepository) {
		this.snapshotEntityRepository = snapshotEntityRepository;
	}

	@Override
	public void saveSnapShot(Snapshot snapshot) {
		snapshotEntityRepository.save(SnapshotEntity.fromSnapshot(snapshot));
	}
}
