package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.services.DcatDatasetService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.services.DcatServerService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.services.EventSourceService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.kafkasource.KafkaSourceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.DuplicateRetentionException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service.ViewValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamClosedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.VersionCreationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class EventStreamServiceImplTest {

	private static final String COLLECTION = "collection";
	private static final String TIMESTAMP_PATH = "generatedAt";
	private static final String VERSION_OF_PATH = "isVersionOf";
	private static final VersionCreationProperties VERSION_CREATION_PROPERTIES = VersionCreationProperties.disabled();
	private static final boolean CLOSED = false;
	private static final EventStream EVENT_STREAM = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_PROPERTIES, null);
	private static final EventStreamTO.Builder BASE_BUILDER = new EventStreamTO.Builder()
			.withEventStream(EVENT_STREAM)
			.withViews(List.of())
			.withShacl(ModelFactory.createDefaultModel())
			.withEventSourceRetentionPolicies(List.of());
	private static final EventStreamTO EVENT_STREAM_RESPONSE = BASE_BUILDER.build();
	private DcatDataset dataset;
	private EventStreamTO eventStreamTOWithDataset;

	@Mock
	private EventStreamRepository eventStreamRepository;
	@Mock
	private KafkaSourceRepository kafkaSourceRepository;
	@Mock
	private ApplicationEventPublisher eventPublisher;
	@Captor
	ArgumentCaptor<EventStreamDeletedEvent> deletedEventArgumentCaptor;
	@Mock
	private ViewValidator viewValidator;
	@Mock
	private ShaclShapeService shaclShapeService;
	@Mock
	private DcatDatasetService dcatDatasetService;
	@Mock
	private DcatServerService dcatServerService;
	@Mock
	private EventSourceService eventSourceService;
	@InjectMocks
	private EventStreamServiceImpl service;

	@BeforeEach
	void setUp() throws URISyntaxException {
		dataset = new DcatDataset(COLLECTION, readModelFromFile("dcat/dataset/valid.ttl"));
		eventStreamTOWithDataset = BASE_BUILDER.withDcatDataset(dataset).build();
	}

	@Test
	void when_retrieveAllEventStream_then_returnList() {
		final String otherCollection = "other";
		List<ViewSpecification> views = List
				.of(new ViewSpecification(new ViewName("other", "view1"), List.of(), List.of(), 100));

		EventStreamTO otherEventStreamTO = new EventStreamTO.Builder()
				.withCollection(otherCollection)
				.withTimestampPath("created")
				.withVersionOfPath("versionOf")
				.withVersionDelimiter(null)
				.withClosed(CLOSED)
				.withViews(views)
				.withShacl(ModelFactory.createDefaultModel())
				.withEventSourceRetentionPolicies(List.of())
				.withDcatDataset(dataset)
				.build();

		Mockito.when(eventStreamRepository.retrieveAllEventStreamTOs()).thenReturn(List.of(eventStreamTOWithDataset, otherEventStreamTO));

		List<EventStreamTO> eventStreams = service.retrieveAllEventStreams();

		assertThat(eventStreams).containsExactlyInAnyOrder(EVENT_STREAM_RESPONSE, otherEventStreamTO);
		Mockito.verify(eventStreamRepository).retrieveAllEventStreamTOs();
		Mockito.verifyNoInteractions(shaclShapeService, dcatDatasetService);

	}

	@Test
	void when_collectionExists_then_retrieveEventStream() {
		Mockito.when(eventStreamRepository.retrieveEventStreamTO(COLLECTION)).thenReturn(Optional.of(eventStreamTOWithDataset));

		EventStreamTO eventStreamTO = service.retrieveEventStream(COLLECTION);

		assertThat(eventStreamTO).isEqualTo(EVENT_STREAM_RESPONSE);
		Mockito.verify(eventStreamRepository).retrieveEventStreamTO(COLLECTION);
		Mockito.verifyNoInteractions(shaclShapeService, dcatDatasetService);
	}

	@Test
	void when_collectionAndDatasetExists_then_retrieveEventStreamWithDataset() {
		Mockito.when(eventStreamRepository.retrieveEventStreamTO(COLLECTION)).thenReturn(Optional.of(eventStreamTOWithDataset));

		EventStreamTO eventStreamTO = service.retrieveEventStream(COLLECTION);

		assertThat(eventStreamTO).isEqualTo(eventStreamTOWithDataset);
	}

	@Test
	void when_collectionDoesNotExist_and_retrieveCollection_then_throwException() {
		Mockito.when(eventStreamRepository.retrieveEventStreamTO(COLLECTION)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.retrieveEventStream(COLLECTION))
				.isInstanceOf(MissingResourceException.class)
				.hasMessage("Resource of type: eventstream with id: %s could not be found.", COLLECTION);

		Mockito.verify(eventStreamRepository).retrieveEventStreamTO(COLLECTION);
		Mockito.verifyNoInteractions(dcatDatasetService, shaclShapeService);
	}

	@Test
	void when_collectionExists_then_updateEventSource() {
		service.updateEventSource(COLLECTION, List.of());

		InOrder inOrder = Mockito.inOrder(eventSourceService, eventPublisher);
		inOrder.verify(eventSourceService).updateEventSource(COLLECTION, List.of());
	}

	@Nested
	class CreateEventStream {
		private static final String TIMESTAMP_PATH = "generatedAt";
		private static final String VERSION_OF_PATH = "versionOf";
		private static final EventStream EVENT_STREAM = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_PROPERTIES, null);

		@Test
		void given_NonExistingEventStream_when_createEventStream_then_expectCreatedEventStream() {
			EventStreamTO eventStreamTO = BASE_BUILDER.build();

			EventStreamTO createdEventStream = service.createEventStream(eventStreamTO);

			assertThat(createdEventStream).isSameAs(eventStreamTO);
			Mockito.verify(eventStreamRepository).saveEventStream(eventStreamTO);
			Mockito.verifyNoInteractions(shaclShapeService);
		}

		@Test
		void given_ExistingEventStream_when_createEventStreamWithSameName_then_throwException() {
			Mockito.when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.of(EVENT_STREAM));
			EventStreamTO eventStreamTO = BASE_BUILDER.build();

			assertThatThrownBy(() -> service.createEventStream(eventStreamTO))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("This collection already exists!");

			InOrder inOrder = Mockito.inOrder(eventStreamRepository, shaclShapeService);
			inOrder.verify(eventStreamRepository).retrieveEventStream(COLLECTION);
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void given_NonExistingEventStream_when_errorOccursWhileCreation_then_DeleteAgainAndThrowException() {
			final String byPage = "by-page";
			final String byLocation = "by-location";
			Mockito.when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.empty());
			Mockito.doThrow(DuplicateRetentionException.class).when(viewValidator).validateView(ArgumentMatchers.any());
			ViewSpecification byPageView = new ViewSpecification(new ViewName(COLLECTION, byPage), List.of(), List.of(), 100);
			ViewSpecification byLocationView = new ViewSpecification(new ViewName(COLLECTION, byLocation), List.of(), List.of(), 100);
			EventStreamTO eventStreamTO = new EventStreamTO.Builder()
					.withEventStream(EVENT_STREAM)
					.withViews(List.of(byPageView, byLocationView))
					.withShacl(ModelFactory.createDefaultModel())
					.withEventSourceRetentionPolicies(List.of())
					.build();

			assertThatThrownBy(() -> service.createEventStream(eventStreamTO))
					.isInstanceOf(DuplicateRetentionException.class);
			Mockito.verify(eventStreamRepository).retrieveEventStream(COLLECTION);
		}
	}


	@Test
	void when_collectionDoesNotExists_and_triesToDelete_then_throwException() {
		Mockito.when(eventStreamRepository.deleteEventStream(COLLECTION)).thenReturn(0);

		assertThatThrownBy(() -> service.deleteEventStream(COLLECTION))
				.isInstanceOf(MissingResourceException.class)
				.hasMessage("Resource of type: eventstream with id: %s could not be found.", COLLECTION);
	}

	@Test
	void when_collectionExists_and_triesToDeleteEventStream_then_throwExceptionWithRetrieval() {
		Mockito.when(eventStreamRepository.deleteEventStream(COLLECTION)).thenReturn(1).thenReturn(0);

		service.deleteEventStream(COLLECTION);

		InOrder inOrder = Mockito.inOrder(eventStreamRepository, eventPublisher);
		inOrder.verify(eventStreamRepository).deleteEventStream(COLLECTION);
		inOrder.verify(eventPublisher).publishEvent(deletedEventArgumentCaptor.capture());
		assertThat(deletedEventArgumentCaptor.getValue()).isEqualTo(new EventStreamDeletedEvent(COLLECTION));
		assertThatThrownBy(() -> service.retrieveEventStream(COLLECTION))
				.isInstanceOf(MissingResourceException.class)
				.hasMessage("Resource of type: eventstream with id: %s could not be found.", COLLECTION);
	}

	@Test
	void when_init() {
		((EventStreamServiceImpl) service).initEventStream();
		Mockito.verify(eventStreamRepository).retrieveAllEventStreams();
	}

	@Test
	void should_CallDcatServiceToGetDcat_when_GetComposedDcatIsCalled() {
		service.getComposedDcat();

		Mockito.verify(dcatServerService).getComposedDcat();
		Mockito.verifyNoMoreInteractions(dcatServerService);
	}

	@Test
	void when_closeEventStream_thenPublishEventStreamClosedEvent() {
		Mockito.when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.of(EVENT_STREAM));

		service.closeEventStream(COLLECTION);

		Mockito.verify(eventStreamRepository).closeEventStream(COLLECTION);
		Mockito.verify(eventStreamRepository).retrieveEventStream(COLLECTION);
		Mockito.verify(eventPublisher).publishEvent(new EventStreamClosedEvent(COLLECTION));
	}

	@Test
	void when_closeEventStream_andEventStreamDoesNotExist_thenExceptionIsThrown() {
		Mockito.when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.closeEventStream(COLLECTION))
				.isInstanceOf(MissingResourceException.class)
				.hasMessage("Resource of type: eventstream with id: %s could not be found.", COLLECTION);

		Mockito.verify(eventStreamRepository).retrieveEventStream(COLLECTION);
		Mockito.verify(eventPublisher, Mockito.never()).publishEvent(new EventStreamClosedEvent(COLLECTION));
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

}
