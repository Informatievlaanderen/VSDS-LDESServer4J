package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.collection.EventStreamCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
	@Mock
	private EventStreamCollection eventStreamCollection;
	@Mock
	private ApplicationEventPublisher eventPublisher;
	@Captor
	ArgumentCaptor<EventStreamDeletedEvent> deletedEventArgumentCaptor;
	@Mock
	private ViewService viewService;
	@Mock
	private ShaclShapeService shaclShapeService;

	private EventStreamService service;

	@BeforeEach
	void setUp() {
		service = new EventStreamServiceImpl(eventStreamCollection, viewService, shaclShapeService, eventPublisher);
	}

	@Test
	void when_retrieveAllEventStream_then_returnList() {
		final String otherCollection = "other";
		EventStream otherEventStream = new EventStream(otherCollection, "created", "versionOf", "memberType");
		List<ViewSpecification> views = List
				.of(new ViewSpecification(new ViewName("other", "view1"), List.of(), List.of()));

		EventStreamResponse otherEventStreamResponse = new EventStreamResponse(otherCollection, "created", "versionOf",
				"memberType",
				views, ModelFactory.createDefaultModel());

		when(eventStreamCollection.retrieveAllEventStreams()).thenReturn(List.of(EVENT_STREAM, otherEventStream));
		when(viewService.getViewsByCollectionName(otherCollection)).thenReturn(views);
		when(viewService.getViewsByCollectionName(COLLECTION)).thenReturn(List.of());

		when(shaclShapeService.retrieveShaclShape(COLLECTION))
				.thenReturn(new ShaclShape(COLLECTION, ModelFactory.createDefaultModel()));
		when(shaclShapeService.retrieveShaclShape(otherCollection))
				.thenReturn(new ShaclShape(otherCollection, ModelFactory.createDefaultModel()));

		List<EventStreamResponse> eventStreams = service.retrieveAllEventStreams();
		List<EventStreamResponse> expectedEventStreams = List.of(EVENT_STREAM_RESPONSE, otherEventStreamResponse);
		assertEquals(expectedEventStreams, eventStreams);

		InOrder inOrder = inOrder(eventStreamCollection, viewService, shaclShapeService);
		inOrder.verify(eventStreamCollection).retrieveAllEventStreams();
		inOrder.verify(viewService).getViewsByCollectionName(COLLECTION);
		inOrder.verify(shaclShapeService).retrieveShaclShape(COLLECTION);
		inOrder.verify(viewService).getViewsByCollectionName(otherCollection);
		inOrder.verify(shaclShapeService).retrieveShaclShape(otherCollection);

	}

	@Test
	void when_collectionExists_then_retrieveShape() {
		when(eventStreamCollection.retrieveEventStream(COLLECTION)).thenReturn(Optional.of(EVENT_STREAM));
		when(viewService.getViewsByCollectionName(COLLECTION)).thenReturn(List.of());
		when(shaclShapeService.retrieveShaclShape(COLLECTION)).thenReturn(new ShaclShape(COLLECTION, ModelFactory.createDefaultModel()));

		assertEquals(EVENT_STREAM_RESPONSE, service.retrieveEventStream(COLLECTION));

		InOrder inOrder = inOrder(eventStreamCollection, viewService, shaclShapeService);
		inOrder.verify(eventStreamCollection).retrieveEventStream(COLLECTION);
		inOrder.verify(viewService).getViewsByCollectionName(COLLECTION);
		inOrder.verify(shaclShapeService).retrieveShaclShape(COLLECTION);
	}

	@Test
	void when_collectionDoesNotExists_then_throwException() {
		when(eventStreamCollection.retrieveEventStream(COLLECTION)).thenReturn(Optional.empty());

		Exception e = assertThrows(MissingEventStreamException.class, () -> service.retrieveEventStream(COLLECTION));
		assertEquals("No event stream found for collection " + COLLECTION, e.getMessage());
		verify(eventStreamCollection).retrieveEventStream(COLLECTION);
		verifyNoInteractions(viewService, shaclShapeService);
	}

	@Test
	void when_collectionExists_and_updateEventStream_then_expectUpdatedEventStream() {
		final String timeStampPath = "generatedAt";
		final String versionOfPath = "versionOf";
		final String memberType = "typeOfMember";
		ShaclShape shaclShape = new ShaclShape(COLLECTION, ModelFactory.createDefaultModel());
		EventStream eventStream = new EventStream(COLLECTION, timeStampPath, versionOfPath, memberType);

		when(eventStreamCollection.saveEventStream(eventStream)).thenReturn(eventStream);
		when(shaclShapeService.updateShaclShape(shaclShape)).thenReturn(shaclShape);
		EventStreamResponse eventStreamResponse = new EventStreamResponse(COLLECTION, timeStampPath, versionOfPath,
				memberType,
				List.of(), ModelFactory.createDefaultModel());

		EventStreamResponse updatedEventStream = service.saveEventStream(eventStreamResponse);

		assertEquals(eventStreamResponse, updatedEventStream);
		InOrder inOrder = inOrder(eventStreamCollection, shaclShapeService, viewService);
		inOrder.verify(eventStreamCollection).saveEventStream(eventStream);
		inOrder.verify(shaclShapeService).updateShaclShape(shaclShape);
		inOrder.verify(viewService).addDefaultView(COLLECTION);
	}

	@Test
	void when_collectionDoesNotExists_and_triesToDelete_then_throwException() {
		when(eventStreamCollection.retrieveEventStream(COLLECTION)).thenReturn(Optional.empty());
		Exception e = assertThrows(MissingEventStreamException.class, () -> service.deleteEventStream(COLLECTION));
		assertEquals("No event stream found for collection " + COLLECTION, e.getMessage());
		verify(eventStreamCollection).retrieveEventStream(COLLECTION);
		verifyNoMoreInteractions(eventStreamCollection);
		verifyNoInteractions(viewService, shaclShapeService, eventPublisher);
	}

	@Test
	void when_collectionExists_and_triesToDeleteEventStream_then_throwExceptionWithRetrieval() {
		when(eventStreamCollection.retrieveEventStream(COLLECTION)).thenReturn(Optional.of(EVENT_STREAM)).thenReturn(Optional.empty());

		service.deleteEventStream(COLLECTION);

		InOrder inOrder = inOrder(eventStreamCollection, eventPublisher);
		inOrder.verify(eventStreamCollection).deleteEventStream(COLLECTION);
		inOrder.verify(eventPublisher).publishEvent(deletedEventArgumentCaptor.capture());
		assertEquals(new EventStreamDeletedEvent(COLLECTION), deletedEventArgumentCaptor.getValue());
		assertThrows(MissingEventStreamException.class, () -> service.retrieveEventStream(COLLECTION));
	}
}
