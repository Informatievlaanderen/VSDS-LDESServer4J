package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.shaclshape.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.shaclshape.entity.ShaclShapeEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;

public class ShaclShapeEntityConverter {
	public ShaclShapeEntity fromShaclShape(ShaclShape shaclShape) {
		String shaclShapeString = RDFWriter.source(shaclShape.getModel()).lang(Lang.TURTLE).asString();
		return new ShaclShapeEntity(shaclShape.getCollection(), shaclShapeString);
	}

	public ShaclShape toShaclShape(ShaclShapeEntity shaclShapeEntity) {
		Model shacl = RDFParserBuilder.create().fromString(shaclShapeEntity.getModel()).lang(Lang.TURTLE).toModel();
		return new ShaclShape(shaclShapeEntity.getId(), shacl);
	}
}
