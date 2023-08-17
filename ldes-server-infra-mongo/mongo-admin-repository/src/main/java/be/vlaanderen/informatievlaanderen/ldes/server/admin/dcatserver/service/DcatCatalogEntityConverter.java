package be.vlaanderen.informatievlaanderen.ldes.server.admin.dcatserver.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.dcatserver.entity.DcatCatalogEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;

public class DcatCatalogEntityConverter {
	public DcatCatalogEntity fromDcatServer(DcatServer dcatServer) {
		final String dcatString = RDFWriter.source(dcatServer.getDcat()).lang(Lang.TURTLE).asString();
		return new DcatCatalogEntity(dcatServer.getId(), dcatString);
	}

	public DcatServer toDcatServer(DcatCatalogEntity dcatCatalogEntity) {
		final Model dcat = RDFParserBuilder.create().fromString(dcatCatalogEntity.getDcat()).lang(Lang.TURTLE)
				.toModel();
		return new DcatServer(dcatCatalogEntity.getId(), dcat);
	}
}
