package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services.DcatDatasetService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.services.DcatServerService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventStreamServiceImplTest {
	private static final String COLLECTION = "collection";
	private static final String TIMESTAMP_PATH = "generatedAt";
	private static final String VERSION_OF_PATH = "isVersionOf";
	private static final String MEMBER_TYPE = "memberType";
	private static final EventStream EVENT_STREAM = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH,
			MEMBER_TYPE);
	private static final EventStreamResponse EVENT_STREAM_RESPONSE = new EventStreamResponse(COLLECTION, TIMESTAMP_PATH,
			VERSION_OF_PATH, MEMBER_TYPE, List.of(), ModelFactory.createDefaultModel());
	private DcatDataset dataset;
	private EventStreamResponse EVENT_STREAM_RESPONSEWITH_DATASET;
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
		EVENT_STREAM_RESPONSEWITH_DATASET = new EventStreamResponse(COLLECTION, TIMESTAMP_PATH,
				VERSION_OF_PATH, MEMBER_TYPE, List.of(), ModelFactory.createDefaultModel(), dataset);
	}

	@Test
	void when_retrieveAllEventStream_then_returnList() {
		final String otherCollection = "other";
		EventStream otherEventStream = new EventStream(otherCollection, "created", "versionOf", "memberType");
		List<ViewSpecification> views = List
				.of(new ViewSpecification(new ViewName("other", "view1"), List.of(), List.of()));

		EventStreamResponse otherEventStreamResponse = new EventStreamResponse(otherCollection, "created", "versionOf",
				"memberType", views, ModelFactory.createDefaultModel(), dataset);

		when(eventStreamRepository.retrieveAllEventStreams()).thenReturn(List.of(EVENT_STREAM, otherEventStream));
		when(viewService.getViewsByCollectionName(otherCollection)).thenReturn(views);
		when(viewService.getViewsByCollectionName(COLLECTION)).thenReturn(List.of());

		when(shaclShapeService.retrieveShaclShape(COLLECTION))
				.thenReturn(new ShaclShape(COLLECTION, ModelFactory.createDefaultModel()));
		when(shaclShapeService.retrieveShaclShape(otherCollection))
				.thenReturn(new ShaclShape(otherCollection, ModelFactory.createDefaultModel()));

		when(dcatDatasetService.retrieveDataset(COLLECTION)).thenReturn(Optional.empty());
		when(dcatDatasetService.retrieveDataset(otherCollection)).thenReturn(Optional.of(dataset));

		List<EventStreamResponse> eventStreams = service.retrieveAllEventStreams();
		List<EventStreamResponse> expectedEventStreams = List.of(EVENT_STREAM_RESPONSE, otherEventStreamResponse);
		assertEquals(expectedEventStreams, eventStreams);

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

		assertEquals(EVENT_STREAM_RESPONSE, service.retrieveEventStream(COLLECTION));

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

		assertEquals(EVENT_STREAM_RESPONSEWITH_DATASET, service.retrieveEventStream(COLLECTION));

		InOrder inOrder = inOrder(eventStreamRepository, viewService, shaclShapeService, dcatDatasetService);
		inOrder.verify(eventStreamRepository).retrieveEventStream(COLLECTION);
		inOrder.verify(viewService).getViewsByCollectionName(COLLECTION);
		inOrder.verify(shaclShapeService).retrieveShaclShape(COLLECTION);
		inOrder.verify(dcatDatasetService).retrieveDataset(COLLECTION);
	}

	@Test
	void when_collectionDoesNotExist_and_retrieveCollection_then_throwException() {
		when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.empty());

		Exception e = assertThrows(MissingEventStreamException.class, () -> service.retrieveEventStream(COLLECTION));
		assertEquals("No event stream found for collection " + COLLECTION, e.getMessage());
		verify(eventStreamRepository).retrieveEventStream(COLLECTION);
		verifyNoInteractions(viewService, shaclShapeService);
	}

	@Test
	void when_collectionExists_and_retrieveMemberType_then_retrieveMemberType() {
		when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.of(EVENT_STREAM));

		String memberType = assertDoesNotThrow(() -> service.retrieveMemberType(COLLECTION));
		assertEquals(MEMBER_TYPE, memberType);
		verify(eventStreamRepository).retrieveEventStream(COLLECTION);
		verifyNoInteractions(viewService, shaclShapeService);
	}

	@Test
	void when_collectionDoesNotExist_and_retrieveMemberType_then_throwException() {
		when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.empty());

		Exception e = assertThrows(MissingEventStreamException.class, () -> service.retrieveMemberType(COLLECTION));
		assertEquals("No event stream found for collection " + COLLECTION, e.getMessage());
		verify(eventStreamRepository).retrieveEventStream(COLLECTION);
		verifyNoInteractions(shaclShapeService, viewService);
	}

	@Test
	void when_updateExistingEventStream_and_defaultViewEnabled_then_expectUpdatedEventStream() {
		final String timeStampPath = "generatedAt";
		final String versionOfPath = "versionOf";
		final String memberType = "typeOfMember";
		ShaclShape shaclShape = new ShaclShape(COLLECTION, ModelFactory.createDefaultModel());
		EventStream eventStream = new EventStream(COLLECTION, timeStampPath, versionOfPath, memberType);

		when(eventStreamRepository.saveEventStream(eventStream)).thenReturn(eventStream);
		when(shaclShapeService.updateShaclShape(shaclShape)).thenReturn(shaclShape);
		EventStreamResponse eventStreamResponse = new EventStreamResponse(COLLECTION, timeStampPath, versionOfPath,
				memberType, List.of(), ModelFactory.createDefaultModel());

		EventStreamResponse updatedEventStream = service.createEventStream(eventStreamResponse);

		assertEquals(eventStreamResponse, updatedEventStream);
		InOrder inOrder = inOrder(eventStreamRepository, shaclShapeService, viewService);
		inOrder.verify(eventStreamRepository).saveEventStream(eventStream);
		inOrder.verify(shaclShapeService).updateShaclShape(shaclShape);
		inOrder.verify(viewService).addDefaultView(COLLECTION);
	}

	@Test
	void when_collectionDoesNotExists_and_triesToDelete_then_throwException() {
		when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.empty());
		Exception e = assertThrows(MissingEventStreamException.class, () -> service.deleteEventStream(COLLECTION));
		assertEquals("No event stream found for collection " + COLLECTION, e.getMessage());
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
		assertEquals(new EventStreamDeletedEvent(COLLECTION), deletedEventArgumentCaptor.getValue());
		assertThrows(MissingEventStreamException.class, () -> service.retrieveEventStream(COLLECTION));
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
