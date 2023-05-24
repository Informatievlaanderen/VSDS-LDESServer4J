package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("server-dcat")
public class ServerDcatEntity {
	@Id
	private final String id;
	private final String dcat;

	public ServerDcatEntity(String id, String dcat) {
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
