package be.vlaanderen.informatievlaanderen.ldes.server.domain.events;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import org.apache.jena.rdf.model.Model;

import java.util.Objects;

public class ShaclChangedEvent {
	private final ShaclShape shacl;

	public ShaclChangedEvent(String collectionName, Model shacl) {
		this.shacl = new ShaclShape(collectionName, shacl);
	}

	public ShaclShape getShacl() {
		return shacl;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ShaclChangedEvent that))
			return false;
		return Objects.equals(shacl, that.shacl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(shacl);
	}
}
