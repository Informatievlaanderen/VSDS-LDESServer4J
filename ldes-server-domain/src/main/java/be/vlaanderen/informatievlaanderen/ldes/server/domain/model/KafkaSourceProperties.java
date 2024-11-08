package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

public record KafkaSourceProperties (String collection, String topic, String mimeType) {
}
