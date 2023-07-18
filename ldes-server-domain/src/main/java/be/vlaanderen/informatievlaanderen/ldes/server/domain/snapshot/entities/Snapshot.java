package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities;

import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public class Snapshot {

	private final String snapshotId;
	private final String collectionName;
	private final Model shape;
	private final LocalDateTime snapshotUntil;
	private final String snapshotOf;

	public Snapshot(String snapshotId, String collectionName, Model shape, LocalDateTime snapshotUntil,
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

	public Model getShape() {
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
