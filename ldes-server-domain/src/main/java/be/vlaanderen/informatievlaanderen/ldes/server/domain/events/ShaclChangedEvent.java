package be.vlaanderen.informatievlaanderen.ldes.server.domain.events;

import org.apache.jena.rdf.model.Model;

import java.util.Objects;

public class ShaclChangedEvent {
	private final String collectionName;
	private final Model shacl;

	public ShaclChangedEvent(String collectionName, Model shacl) {
		this.collectionName = collectionName;
		this.shacl = shacl;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public Model getShacl() {
		return shacl;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ShaclChangedEvent that))
			return false;
		return Objects.equals(collectionName, that.collectionName) && shacl.isIsomorphicWith(that.shacl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(collectionName, shacl);
	}
}
