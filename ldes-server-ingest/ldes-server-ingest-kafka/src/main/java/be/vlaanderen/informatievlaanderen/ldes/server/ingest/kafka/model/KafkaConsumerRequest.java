package be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.model;

public record KafkaConsumerRequest(String topic, String mimeType, boolean startImmediately) {
}
