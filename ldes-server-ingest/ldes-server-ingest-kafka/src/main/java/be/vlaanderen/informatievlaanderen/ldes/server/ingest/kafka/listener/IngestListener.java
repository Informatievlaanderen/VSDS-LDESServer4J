package be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.listener;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.MemberIngester;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.IngestValidator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

public class IngestListener implements AcknowledgingMessageListener<String, String> {
	Logger log = LoggerFactory.getLogger(IngestListener.class);

	private final String collection;
	private final Lang lang;
	private final IngestValidator validator;
	private final MemberIngester memberIngester;

	public IngestListener(String collection, Lang lang, IngestValidator validator, MemberIngester memberIngester) {
		this.collection = collection;
		this.lang = lang;
		this.validator = validator;
		this.memberIngester = memberIngester;
	}

	@Override
	public void onMessage(ConsumerRecord<String, String> data, Acknowledgment ack) {
		try {
			var model = RDFParser.fromString(data.value(), lang).toModel();

			validator.validate(model, collection);
			memberIngester.ingest(collection, model);

			ack.acknowledge();
		} catch (Exception e) {
			log.error(e.toString());
		}
	}
}
