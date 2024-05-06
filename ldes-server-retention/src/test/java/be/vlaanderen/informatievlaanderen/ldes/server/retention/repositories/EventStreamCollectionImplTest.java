package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.valueobjects.EventStreamProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventStreamCollectionImplTest {

	private final static String COLLECTION = "COLLECTION";
	private final static String TIMESTAMP_PATH = "http://www.w3.org/ns/prov#generatedAtTime";
	private final static String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";
	private final static boolean VERSION_CREATION_ENABLED = false;
	private final EventStreamCollectionImpl eventStreamCollection = new EventStreamCollectionImpl();

	@Test
	void when_EventStreamDoesNotExist_then_MissingEventStreamExceptionIsThrown() {
		eventStreamCollection.handleEventStreamCreatedEvent(
				new EventStreamCreatedEvent(new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_ENABLED, List.of())));
		eventStreamCollection.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(COLLECTION));

		assertThatThrownBy(() -> eventStreamCollection.getEventStreamProperties(COLLECTION))
				.isInstanceOf(MissingResourceException.class)
				.hasMessage("Resource of type: eventstream with id: %s could not be found.", COLLECTION);
	}

	@Test
	void when_EventStreamExists_then_EventStreamPropertiesCanBeRetrieved() {
		eventStreamCollection.handleEventStreamCreatedEvent(
				new EventStreamCreatedEvent(new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_ENABLED, List.of())));

		EventStreamProperties actualEventStreamProperties = eventStreamCollection.getEventStreamProperties(COLLECTION);

		EventStreamProperties expectedEventStreamProperties = new EventStreamProperties(VERSION_OF_PATH,
				TIMESTAMP_PATH);

		assertThat(actualEventStreamProperties).isEqualTo(expectedEventStreamProperties);
	}

}
