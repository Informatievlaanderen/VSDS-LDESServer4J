package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

public interface LdesStreamModelService {
	String retrieveShape(String collectionName);
	String updateShape(String collectionName, String shape);

}
