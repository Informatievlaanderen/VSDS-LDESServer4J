package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dcat_catalog")
public class DcatCatalogEntity {
	@Id
	private String id;
	private String dcat;

	protected DcatCatalogEntity() {
	}

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
