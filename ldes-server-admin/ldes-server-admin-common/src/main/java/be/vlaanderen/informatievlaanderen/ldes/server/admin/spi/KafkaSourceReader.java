package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.KafkaSourceProperties;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

@Component
public class KafkaSourceReader {
	private static final String KAFKA_SOURCE = "kafkaSource";
	private static final String KAFKA_BEAN = "kafkaListenerContainerManager";
	private static final String KAFKA_TOPIC = "topic";
	private static final String KAFKA_MIME_TYPE = "mimeType";
	private final DefaultListableBeanFactory beanFactory;

	public KafkaSourceReader(DefaultListableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public KafkaSourceProperties readKafkaSourceProperties(String collection, Model model) {

		var kafkaSourceStmts = model.listObjectsOfProperty(null, createProperty(LDES, KAFKA_SOURCE));
		if (!kafkaSourceStmts.hasNext()) {
			return null;
		}

		checkKafkaIngestModuleEnabled();

		try {
			var kafkaSource = kafkaSourceStmts.next();
			String topic = model.listObjectsOfProperty(kafkaSource.asResource(), createProperty(LDES, KAFKA_TOPIC))
					.next()
					.asLiteral()
					.getString();
			String mimeType = model.listObjectsOfProperty(kafkaSource.asResource(), createProperty(LDES, KAFKA_MIME_TYPE))
					.next()
					.asLiteral()
					.getString();

			return new KafkaSourceProperties(collection, topic, mimeType);
		}
		catch (NoSuchElementException e) {
			throw new IllegalArgumentException("KafkaSource properties are missing");
		}
	}

	private void checkKafkaIngestModuleEnabled() {
		if (!beanFactory.containsBean(KAFKA_BEAN)) {
			throw new IllegalStateException("Kafka Ingest module is not enabled");
		}
	}
}
