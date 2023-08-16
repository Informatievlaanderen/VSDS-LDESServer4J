package be.vlaanderen.informatievlaanderen.ldes.server.domain.events;

import org.apache.jena.rdf.model.Model;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ShaclChangedEvent {

	private final String collection;
	private final Model model;

	public ShaclChangedEvent(String collectionName, Model shacl) {
		this.collection = collectionName;
		this.model = shacl;
	}

	public String getCollection() {
		return collection;
	}

	public Model getModel() {
		return model;
	}

	// TODO TVB: 16/08/23 delete
	public static void main(String[] args) {
		CompletableFuture.runAsync(() -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			System.out.println("yay");
		});

		System.out.println("end");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ShaclChangedEvent that))
			return false;
		return Objects.equals(collection, that.collection);
	}

	@Override
	public int hashCode() {
		return Objects.hash(collection);
	}
}
