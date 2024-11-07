package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin;

public record KafkaSourceAddedEvent(String collection, String topic, String mimeType) {
}
