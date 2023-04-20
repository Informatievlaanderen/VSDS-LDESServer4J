package be.vlaanderen.informatievlaanderen.ldes.server.domain.events;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;

import java.util.Objects;

public class ShaclChangedEvent {
	private final ShaclShape shacl;

	public ShaclChangedEvent(ShaclShape shacl) {
		this.shacl = shacl;
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
