package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatserver.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("dcat_catalog")
public class DcatCatalogEntity {
	@Id
	private final String id;
	private final String dcat;

	public DcatCatalogEntity(String id, String dcat) {
		this.id = id;
		this.dcat = dcat;
	}

	public String getId() {
		return id;
	}

	public String getDcat() {
		return dcat;
	}

	public DcatServer toDcatServer() {
		return new DcatServer(this.id, RDFParser.fromString(this.dcat)
				.lang(Lang.TURTLE)
				.toModel());
	}
}
