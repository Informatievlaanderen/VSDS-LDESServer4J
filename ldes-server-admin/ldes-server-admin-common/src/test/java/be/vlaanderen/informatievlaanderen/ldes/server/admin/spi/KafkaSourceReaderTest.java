package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.KafkaSourceProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import static org.junit.jupiter.api.Assertions.*;

class KafkaSourceReaderTest {

	private KafkaSourceReader kafkaSourceReader;
	private DefaultListableBeanFactory beanFactory;

	@BeforeEach
	void setup() {
		beanFactory = new DefaultListableBeanFactory();
		kafkaSourceReader = new KafkaSourceReader(beanFactory);
	}

	@Test
	void readKafkaSourceProperties_NoKafkaSourcePresent() {
		KafkaSourceProperties kafkaSourceProperties = kafkaSourceReader.readKafkaSourceProperties("collection", ModelFactory.createDefaultModel());
		assertNull(kafkaSourceProperties);
	}

	@Test
	void readKafkaSourceProperties_KafkaIngestNotEnabled() {
		final Model eventStreamModel = RDFDataMgr.loadModel("eventstream/streams/with-kafka-source/ldes-with-kafkaES.ttl");
		assertThrows(IllegalStateException.class, ()-> kafkaSourceReader.readKafkaSourceProperties("collection", eventStreamModel));
	}

	@Test
	void readKafkaSourceProperties_KafkaIngestEnabled() {
		beanFactory.registerSingleton("kafkaListenerContainerManager", Object.class);
		final Model eventStreamModel = RDFDataMgr.loadModel("eventstream/streams/with-kafka-source/ldes-with-kafkaES.ttl");
		KafkaSourceProperties kafkaSourceProperties = kafkaSourceReader.readKafkaSourceProperties("collection", eventStreamModel);
		assertNotNull(kafkaSourceProperties);
		assertEquals("testTopic", kafkaSourceProperties.topic());
		assertEquals("application/n-quads", kafkaSourceProperties.mimeType());
		assertEquals("collection", kafkaSourceProperties.collection());
	}

	@ParameterizedTest()
	@ValueSource(strings = {"ldes-with-kafkaES-noTopic.ttl", "ldes-with-kafkaES-noMime.ttl"})
	void readKafkaSourceProperties_PropertyMissing(String fileName) {
		beanFactory.registerSingleton("kafkaListenerContainerManager", Object.class);
		final Model eventStreamModel = RDFDataMgr.loadModel("eventstream/streams/with-kafka-source/" + fileName);
		assertThrows(IllegalArgumentException.class, ()-> kafkaSourceReader.readKafkaSourceProperties("collection", eventStreamModel));
	}

}