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
	private final DefaultListableBeanFactory beanFactory;

	public KafkaSourceReader(DefaultListableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public KafkaSourceProperties readKafkaSourceProperties(Model model) {

		var kafkaSourceStmts = model.listObjectsOfProperty(null, createProperty(LDES, "kafkaSource"));
		if (!kafkaSourceStmts.hasNext()) {
			return null;
		}

		// Check if bean of type KafkaListenerContainerManager exists
		if (!beanFactory.containsBean("kafkaListenerContainerManager")) {
			throw new IllegalStateException("Kafka Ingest module is not enabled");
		}


		try {
			var kafkaSource = kafkaSourceStmts.next();
			String topic = model.listObjectsOfProperty(kafkaSource.asResource(), createProperty(LDES, "topic"))
					.next()
					.asLiteral()
					.getString();
			String mimeType = model.listObjectsOfProperty(kafkaSource.asResource(), createProperty(LDES, "mimeType"))
					.next()
					.asLiteral()
					.getString();

			return new KafkaSourceProperties(topic, mimeType);
		}
		catch (NoSuchElementException e) {
			throw new IllegalArgumentException("KafkaSource properties are missing");
		}
	}
}
