package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class CollectionNotFoundException extends RuntimeException {
	public CollectionNotFoundException(String collectionName) {
		super("Collection with name %s could not be found.".formatted(collectionName));
	}
}
