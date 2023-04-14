package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository.SnapshotRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot.entity.SnapshotEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot.repository.SnapshotEntityRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SnapshotMongoRepository implements SnapshotRepository {
	private final SnapshotEntityRepository snapshotEntityRepository;

	public SnapshotMongoRepository(SnapshotEntityRepository snapshotEntityRepository) {
		this.snapshotEntityRepository = snapshotEntityRepository;
	}

	@Override
	public void saveSnapShot(Snapshot snapshot) {
		snapshotEntityRepository.save(SnapshotEntity.fromSnapshot(snapshot));
	}

	@Override
	public Optional<Snapshot> getLastSnapshot() {
		return snapshotEntityRepository.findAll().stream().map(SnapshotEntity::toSnapshot).max(Comparator.comparing(Snapshot::getSnapshotUntil));
	}
}
