package be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.exception;

public class KafkaConsumerException extends RuntimeException {
	public KafkaConsumerException(String message) {
		super(message);

	}
}
