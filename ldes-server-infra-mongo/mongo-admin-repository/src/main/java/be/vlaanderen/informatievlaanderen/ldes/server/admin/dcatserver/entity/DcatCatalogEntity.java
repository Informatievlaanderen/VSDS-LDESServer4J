package be.vlaanderen.informatievlaanderen.ldes.server.admin.dcatserver.entity;

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
}
