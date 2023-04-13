package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities;

import java.time.LocalDateTime;

public class Snapshot {

	private final String snapshotId;
	private final String shape;
	private final LocalDateTime snapshotUntil;
	private final String snapshotOf;
	private final String collectionName;

	public Snapshot(String collectionName, String snapshotId, String shape, LocalDateTime snapshotUntil,
			String snapshotOf) {
		this.collectionName = collectionName;
		this.snapshotId = snapshotId;
		this.shape = shape;
		this.snapshotUntil = snapshotUntil;
		this.snapshotOf = snapshotOf;
	}

	public String getSnapshotId() {
		return snapshotId;
	}

	public String getShape() {
		return shape;
	}

	public LocalDateTime getSnapshotUntil() {
		return snapshotUntil;
	}

	public String getSnapshotOf() {
		return snapshotOf;
	}

	public String getCollectionName() {
		return collectionName;
	}

}
