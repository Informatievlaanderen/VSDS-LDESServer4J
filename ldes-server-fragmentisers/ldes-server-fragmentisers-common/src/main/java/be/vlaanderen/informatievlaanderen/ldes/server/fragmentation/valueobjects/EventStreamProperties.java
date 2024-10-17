package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

public record EventStreamProperties(
		String collectionName,
		String versionOfPath,
		String timestampPath,
		boolean versionCreationEnabled
) {
}
