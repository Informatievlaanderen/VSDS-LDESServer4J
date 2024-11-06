package be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.listener;

import org.apache.jena.riot.Lang;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

public class IngestListener implements AcknowledgingMessageListener<String, String> {
	Logger log = LoggerFactory.getLogger(IngestListener.class);

	private final Lang lang;

	public IngestListener(Lang lang) {
		this.lang = lang;
	}

	@Override
	public void onMessage(ConsumerRecord<String, String> data, Acknowledgment ack) {
		try {
			log.info("Received new message: topic={}, value={}, should be type {}", data.topic(), data.value(), lang.getContentType());
			ack.acknowledge();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
