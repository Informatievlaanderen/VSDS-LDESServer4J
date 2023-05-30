package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.entities;

import org.apache.jena.rdf.model.Model;

import java.util.Objects;

public class DcatServer {
	private final String id;
	private final Model dcat;

	public DcatServer(String id, Model dcat) {
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
		DcatServer that = (DcatServer) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
