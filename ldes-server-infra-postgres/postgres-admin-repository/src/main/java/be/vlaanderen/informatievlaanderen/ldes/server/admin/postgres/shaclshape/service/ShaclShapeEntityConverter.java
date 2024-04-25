package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.entity.ShaclShapeEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.PostgresAdminAutoConfiguration.SERIALISATION_LANG;

public class ShaclShapeEntityConverter {
	public ShaclShapeEntity fromShaclShape(ShaclShape shaclShape) {
		String shaclShapeString = RDFWriter.source(shaclShape.getModel())
				.lang(SERIALISATION_LANG)
				.asString();
		return new ShaclShapeEntity(shaclShape.getCollection(), shaclShapeString);
	}

	public ShaclShape toShaclShape(ShaclShapeEntity shaclShapeEntity) {
		Model shacl = RDFParser.create()
				.fromString(shaclShapeEntity.getModel())
				.lang(SERIALISATION_LANG)
				.toModel();
		return new ShaclShape(shaclShapeEntity.getId(), shacl);
	}
}
