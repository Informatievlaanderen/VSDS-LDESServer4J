package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.entity.DcatCatalogEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.PostgresAdminConstants.SERIALISATION_LANG;

public class DcatCatalogEntityConverter {
	public DcatCatalogEntity fromDcatServer(DcatServer dcatServer) {
		final String dcatString = RDFWriter.source(dcatServer.getDcat())
				.lang(SERIALISATION_LANG)
				.asString();
		return new DcatCatalogEntity(dcatServer.getId(), dcatString);
	}

	public DcatServer toDcatServer(DcatCatalogEntity dcatCatalogEntity) {
		final Model dcat = RDFParserBuilder.create()
				.fromString(dcatCatalogEntity.getDcat())
				.lang(SERIALISATION_LANG)
				.toModel();
		return new DcatServer(dcatCatalogEntity.getId(), dcat);
	}
}
