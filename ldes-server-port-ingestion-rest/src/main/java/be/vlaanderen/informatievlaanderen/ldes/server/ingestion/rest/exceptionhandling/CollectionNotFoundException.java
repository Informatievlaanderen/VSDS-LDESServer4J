package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.exceptionhandling;

public class CollectionNotFoundException extends RuntimeException {
	public CollectionNotFoundException(String collectionName) {
		super("Collection with name %s could not be found.".formatted(collectionName));
	}
}
