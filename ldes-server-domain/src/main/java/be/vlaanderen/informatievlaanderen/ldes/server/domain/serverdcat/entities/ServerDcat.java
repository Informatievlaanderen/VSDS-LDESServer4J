package be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.entities;

import org.apache.jena.rdf.model.Model;

import java.util.Objects;

public class ServerDcat {
	private final String id;
	private final Model dcat;

	public ServerDcat(String id, Model dcat) {
		this.id = id;
		this.dcat = dcat;
	}

	public String getId() {
		return id;
	}

	public Model getDcat() {
		return dcat;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ServerDcat that = (ServerDcat) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
