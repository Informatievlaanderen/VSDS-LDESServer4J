package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotEntityTest {

	@Test
	void test_ConversionFromAndToDomain() {
		Snapshot snapshot = new Snapshot("id", "shape", LocalDateTime.now(), "snapshotOf");

		Snapshot convertedSnapshot = SnapshotEntity.fromSnapshot(snapshot).toSnapshot();

		assertEquals(snapshot.getSnapshotId(), convertedSnapshot.getSnapshotId());
		assertEquals(snapshot.getSnapshotUntil(), convertedSnapshot.getSnapshotUntil());
		assertEquals(snapshot.getSnapshotOf(), convertedSnapshot.getSnapshotOf());
		assertEquals(snapshot.getShape(), convertedSnapshot.getShape());
	}

}