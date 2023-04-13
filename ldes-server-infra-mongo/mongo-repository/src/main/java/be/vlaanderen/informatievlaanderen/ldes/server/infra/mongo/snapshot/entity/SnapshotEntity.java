package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("snapshot")
public class SnapshotEntity {
	@Id
	private String snapshotId;
	private String collectionName;
	private String shape;
	private LocalDateTime snapshotUntil;
	private String snapshotOf;

	public SnapshotEntity(String snapshotId, String collectionName, String shape, LocalDateTime snapshotUntil,
			String snapshotOf) {
		this.collectionName = collectionName;
		this.snapshotId = snapshotId;
		this.shape = shape;
		this.snapshotUntil = snapshotUntil;
		this.snapshotOf = snapshotOf;
	}

	public Snapshot toSnapshot() {
		return new Snapshot(this.snapshotId, this.collectionName, this.shape, this.snapshotUntil, this.snapshotOf);
	}

	public static SnapshotEntity fromSnapshot(Snapshot snapshot) {
		return new SnapshotEntity(snapshot.getSnapshotId(), snapshot.getCollectionName(), snapshot.getShape(),
				snapshot.getSnapshotUntil(),
				snapshot.getSnapshotOf());
	}
}
