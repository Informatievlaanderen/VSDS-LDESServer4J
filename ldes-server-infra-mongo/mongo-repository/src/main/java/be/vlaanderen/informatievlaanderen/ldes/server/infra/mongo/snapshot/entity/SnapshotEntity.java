package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.snapshot.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;
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
		Model shapeModel = RDFParserBuilder.create().fromString(this.shape).lang(Lang.TURTLE).toModel();
		return new Snapshot(this.snapshotId, this.collectionName, shapeModel, this.snapshotUntil, this.snapshotOf);
	}

	public static SnapshotEntity fromSnapshot(Snapshot snapshot) {
		String shapeString = RDFWriter.source(snapshot.getShape()).lang(Lang.TURTLE).asString();
		return new SnapshotEntity(snapshot.getSnapshotId(), snapshot.getCollectionName(), shapeString,
				snapshot.getSnapshotUntil(),
				snapshot.getSnapshotOf());
	}
}
