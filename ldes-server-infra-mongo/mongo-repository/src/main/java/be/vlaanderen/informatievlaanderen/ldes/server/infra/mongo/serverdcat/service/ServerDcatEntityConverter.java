package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.entities.ServerDcat;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat.entity.ServerDcatEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;

public class ServerDcatEntityConverter {
	public ServerDcatEntity fromServerDcat(ServerDcat serverDcat) {
		final String dcatString = RDFWriter.source(serverDcat.getDcat()).lang(Lang.TURTLE).asString();
		return new ServerDcatEntity(serverDcat.getId(), dcatString);
	}

	public ServerDcat toServerDcat(ServerDcatEntity serverDcatEntity) {
		final Model dcat = RDFParserBuilder.create().fromString(serverDcatEntity.getDcat()).lang(Lang.TURTLE).toModel();
		return new ServerDcat(serverDcatEntity.getId(), dcat);
	}
}
