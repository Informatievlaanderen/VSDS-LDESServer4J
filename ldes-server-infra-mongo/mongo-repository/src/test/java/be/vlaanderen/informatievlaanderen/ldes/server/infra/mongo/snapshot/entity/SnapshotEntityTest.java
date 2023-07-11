package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.Snapshot;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnapshotEntityTest {

	@Test
	void test_ConversionFromAndToDomain() {
		Snapshot snapshot = new Snapshot("id", "collectionName", ModelFactory.createDefaultModel(), LocalDateTime.now(),
				"snapshotOf");

		Snapshot convertedSnapshot = SnapshotEntity.fromSnapshot(snapshot).toSnapshot();

		assertEquals(snapshot.getSnapshotId(), convertedSnapshot.getSnapshotId());
		assertEquals(snapshot.getSnapshotUntil(), convertedSnapshot.getSnapshotUntil());
		assertEquals(snapshot.getSnapshotOf(), convertedSnapshot.getSnapshotOf());
		assertTrue(snapshot.getShape().isIsomorphicWith(convertedSnapshot.getShape()));
		assertEquals(snapshot.getCollectionName(), convertedSnapshot.getCollectionName());
	}

}