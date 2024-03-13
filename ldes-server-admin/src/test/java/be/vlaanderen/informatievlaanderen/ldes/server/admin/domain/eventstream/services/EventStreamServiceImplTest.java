package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.services.DcatDatasetService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.services.DcatServerService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.DuplicateRetentionException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventStreamServiceImplTest {

	private static final String COLLECTION = "collection";
	private static final String TIMESTAMP_PATH = "generatedAt";
	private static final String VERSION_OF_PATH = "isVersionOf";
	private static final boolean VERSION_CREATION_ENABLED = false;
	private static final EventStream EVENT_STREAM = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_ENABLED);
	private static final EventStreamTO EVENT_STREAM_RESPONSE = new EventStreamTO(COLLECTION, TIMESTAMP_PATH,
			VERSION_OF_PATH, VERSION_CREATION_ENABLED, List.of(), ModelFactory.createDefaultModel());
	private DcatDataset dataset;
	private EventStreamTO eventStreamTOWithDataset;

	@Mock
	private EventStreamRepository eventStreamRepository;
	@Mock
	private ApplicationEventPublisher eventPublisher;
	@Captor
	ArgumentCaptor<EventStreamDeletedEvent> deletedEventArgumentCaptor;
	@Mock
	private ViewService viewService;
	@Mock
	private ShaclShapeService shaclShapeService;
	@Mock
	private DcatDatasetService dcatDatasetService;
	@Mock
	private DcatServerService dcatServerService;

	private EventStreamService service;

	@BeforeEach
	void setUp() throws URISyntaxException {
		service = new EventStreamServiceImpl(eventStreamRepository, viewService, shaclShapeService, dcatDatasetService,
				dcatServerService,
				eventPublisher);

		dataset = new DcatDataset(COLLECTION, readModelFromFile("dcat-dataset/valid.ttl"));
		eventStreamTOWithDataset = new EventStreamTO(COLLECTION, TIMESTAMP_PATH,
				VERSION_OF_PATH, VERSION_CREATION_ENABLED, List.of(), ModelFactory.createDefaultModel(), dataset);
	}

	@Test
	void when_retrieveAllEventStream_then_returnList() {
		final String otherCollection = "other";
		EventStream otherEventStream = new EventStream(otherCollection, "created", "versionOf", false);
		List<ViewSpecification> views = List
				.of(new ViewSpecification(new ViewName("other", "view1"), List.of(), List.of(), 100));

		EventStreamTO otherEventStreamTO = new EventStreamTO(otherCollection, "created", "versionOf", false,
				views, ModelFactory.createDefaultModel(), dataset);

		when(eventStreamRepository.retrieveAllEventStreams()).thenReturn(List.of(EVENT_STREAM, otherEventStream));
		when(viewService.getViewsByCollectionName(otherCollection)).thenReturn(views);
		when(viewService.getViewsByCollectionName(COLLECTION)).thenReturn(List.of());

		when(shaclShapeService.retrieveShaclShape(COLLECTION))
				.thenReturn(new ShaclShape(COLLECTION, ModelFactory.createDefaultModel()));
		when(shaclShapeService.retrieveShaclShape(otherCollection))
				.thenReturn(new ShaclShape(otherCollection, ModelFactory.createDefaultModel()));

		when(dcatDatasetService.retrieveDataset(COLLECTION)).thenReturn(Optional.empty());
		when(dcatDatasetService.retrieveDataset(otherCollection)).thenReturn(Optional.of(dataset));

		List<EventStreamTO> eventStreams = service.retrieveAllEventStreams();

		assertThat(eventStreams).containsExactlyInAnyOrder(EVENT_STREAM_RESPONSE, otherEventStreamTO);
		InOrder inOrder = inOrder(eventStreamRepository, viewService, shaclShapeService, dcatDatasetService);
		inOrder.verify(eventStreamRepository).retrieveAllEventStreams();
		inOrder.verify(viewService).getViewsByCollectionName(COLLECTION);
		inOrder.verify(shaclShapeService).retrieveShaclShape(COLLECTION);
		inOrder.verify(dcatDatasetService).retrieveDataset(COLLECTION);
		inOrder.verify(viewService).getViewsByCollectionName(otherCollection);
		inOrder.verify(shaclShapeService).retrieveShaclShape(otherCollection);
		inOrder.verify(dcatDatasetService).retrieveDataset(otherCollection);

	}

	@Test
	void when_collectionExists_then_retrieveEventStream() {
		when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.of(EVENT_STREAM));
		when(viewService.getViewsByCollectionName(COLLECTION)).thenReturn(List.of());
		when(shaclShapeService.retrieveShaclShape(COLLECTION)).thenReturn(
				new ShaclShape(COLLECTION, ModelFactory.createDefaultModel()));
		when(dcatDatasetService.retrieveDataset(COLLECTION)).thenReturn(Optional.empty());

		EventStreamTO eventStreamTO = service.retrieveEventStream(COLLECTION);

		assertThat(eventStreamTO).isEqualTo(EVENT_STREAM_RESPONSE);
		InOrder inOrder = inOrder(eventStreamRepository, viewService, shaclShapeService, dcatDatasetService);
		inOrder.verify(eventStreamRepository).retrieveEventStream(COLLECTION);
		inOrder.verify(viewService).getViewsByCollectionName(COLLECTION);
		inOrder.verify(shaclShapeService).retrieveShaclShape(COLLECTION);
		inOrder.verify(dcatDatasetService).retrieveDataset(COLLECTION);
	}

	@Test
	void when_collectionAndDatasetExists_then_retrieveEventStreamWithDataset() {
		when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.of(EVENT_STREAM));
		when(viewService.getViewsByCollectionName(COLLECTION)).thenReturn(List.of());
		when(shaclShapeService.retrieveShaclShape(COLLECTION)).thenReturn(
				new ShaclShape(COLLECTION, ModelFactory.createDefaultModel()));
		when(dcatDatasetService.retrieveDataset(COLLECTION)).thenReturn(Optional.of(dataset));


		EventStreamTO eventStreamTO = service.retrieveEventStream(COLLECTION);

		assertThat(eventStreamTO).isEqualTo(eventStreamTOWithDataset);
		InOrder inOrder = inOrder(eventStreamRepository, viewService, shaclShapeService, dcatDatasetService);
		inOrder.verify(eventStreamRepository).retrieveEventStream(COLLECTION);
		inOrder.verify(viewService).getViewsByCollectionName(COLLECTION);
		inOrder.verify(shaclShapeService).retrieveShaclShape(COLLECTION);
		inOrder.verify(dcatDatasetService).retrieveDataset(COLLECTION);
	}

	@Test
	void when_collectionDoesNotExist_and_retrieveCollection_then_throwException() {
		when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.retrieveEventStream(COLLECTION))
				.isInstanceOf(MissingResourceException.class)
				.hasMessage("Resource of type: eventstream with id: %s could not be found.", COLLECTION);

		verify(eventStreamRepository).retrieveEventStream(COLLECTION);
		verifyNoInteractions(viewService, shaclShapeService);
	}

	@Nested
	class CreateEventStream {
		private static final String TIMESTAMP_PATH = "generatedAt";
		private static final String VERSION_OF_PATH = "versionOf";
		private static final EventStream EVENT_STREAM = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_CREATION_ENABLED);

		@Test
		void given_NonExistingEventStream_when_createEventStream_then_expectCreatedEventStream() {
			ShaclShape shaclShape = new ShaclShape(COLLECTION, ModelFactory.createDefaultModel());
			when(eventStreamRepository.saveEventStream(EVENT_STREAM)).thenReturn(EVENT_STREAM);
			when(shaclShapeService.updateShaclShape(shaclShape)).thenReturn(shaclShape);
			EventStreamTO eventStreamTO = new EventStreamTO(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH,
					VERSION_CREATION_ENABLED, List.of(), ModelFactory.createDefaultModel());

			EventStreamTO createdEventStream = service.createEventStream(eventStreamTO);

			assertThat(createdEventStream).isEqualTo(eventStreamTO);
			InOrder inOrder = inOrder(eventStreamRepository, shaclShapeService, viewService);
			inOrder.verify(eventStreamRepository).retrieveEventStream(COLLECTION);
			inOrder.verify(eventStreamRepository).saveEventStream(EVENT_STREAM);
			inOrder.verify(shaclShapeService).updateShaclShape(shaclShape);
		}

		@Test
		void given_ExistingEventStream_when_createEventStreamWithSameName_then_throwException() {
			when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.of(EVENT_STREAM));
			EventStreamTO eventStreamTO = new EventStreamTO(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH,
					VERSION_CREATION_ENABLED, List.of(), ModelFactory.createDefaultModel());

			assertThatThrownBy(() -> service.createEventStream(eventStreamTO))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("This collection already exists!");

			InOrder inOrder = inOrder(eventStreamRepository, shaclShapeService, viewService);
			inOrder.verify(eventStreamRepository).retrieveEventStream(COLLECTION);
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void given_NonExistingEventStream_when_errorOccursWhileCreation_then_DeleteAgainAndThrowException() {
			final String byPage = "by-page";
			final String byLocation = "by-location";
			ShaclShape shaclShape = new ShaclShape(COLLECTION, ModelFactory.createDefaultModel());
			when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.empty());
			ViewSpecification byPageView = new ViewSpecification(new ViewName(COLLECTION, byPage), List.of(), List.of(), 100);
			ViewSpecification byLocationView = new ViewSpecification(new ViewName(COLLECTION, byLocation), List.of(), List.of(), 100);
			EventStreamTO eventStreamTO = new EventStreamTO(
					COLLECTION,
					TIMESTAMP_PATH,
					VERSION_OF_PATH,
					VERSION_CREATION_ENABLED,
					List.of(byPageView, byLocationView),
					ModelFactory.createDefaultModel());


			doNothing().when(viewService).addView(byPageView);
			doThrow(new DuplicateRetentionException()).when(viewService).addView(byLocationView);

			assertThatThrownBy(() -> service.createEventStream(eventStreamTO))
					.isInstanceOf(DuplicateRetentionException.class);
			InOrder inOrder = inOrder(eventStreamRepository, shaclShapeService, viewService, eventPublisher);
			inOrder.verify(eventStreamRepository).retrieveEventStream(COLLECTION);
			inOrder.verify(shaclShapeService).updateShaclShape(shaclShape);
			inOrder.verify(viewService).addView(byPageView);
			inOrder.verify(viewService).addView(byLocationView);
			inOrder.verify(eventStreamRepository).deleteEventStream(COLLECTION);
			inOrder.verify(eventPublisher).publishEvent(deletedEventArgumentCaptor.capture());
			assertThat(deletedEventArgumentCaptor.getValue()).isEqualTo(new EventStreamDeletedEvent(COLLECTION));
		}
	}


	@Test
	void when_collectionDoesNotExists_and_triesToDelete_then_throwException() {
		when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.deleteEventStream(COLLECTION))
				.isInstanceOf(MissingResourceException.class)
				.hasMessage("Resource of type: eventstream with id: %s could not be found.", COLLECTION);

		verify(eventStreamRepository).retrieveEventStream(COLLECTION);
		verifyNoMoreInteractions(eventStreamRepository);
		verifyNoInteractions(viewService, shaclShapeService, eventPublisher);
	}

	@Test
	void when_collectionExists_and_triesToDeleteEventStream_then_throwExceptionWithRetrieval() {
		when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.of(EVENT_STREAM))
				.thenReturn(Optional.empty());

		service.deleteEventStream(COLLECTION);

		InOrder inOrder = inOrder(eventStreamRepository, eventPublisher);
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
		verify(eventStreamRepository).retrieveAllEventStreams();
	}

	@Test
	void should_CallDcatServiceToGetDcat_when_GetComposedDcatIsCalled() {
		service.getComposedDcat();

		verify(dcatServerService).getComposedDcat();
		verifyNoMoreInteractions(dcatServerService);
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

}
