package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingShaclShapeException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShaclShapeServiceImplTest {
	private static final String COLLECTION_NAME_1 = "collectionName1";
	private ShaclShapeService service;
	@Mock
	private ShaclShapeRepository repository;
	@Captor
	ArgumentCaptor<ShaclChangedEvent> shaclChangedEventArgumentCaptor;
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@BeforeEach
	void setUp() {
		service = new ShaclShapeServiceImpl(repository, applicationEventPublisher);
	}

	@Test
	void when_collectionExists_and_hasShapeConfigured_then_retrieveShape() throws URISyntaxException {
		final Model shape = readModelFromFile("eventstream/streams/example-shape.ttl");

		when(repository.retrieveShaclShape(COLLECTION_NAME_1)).thenReturn(Optional.of(new ShaclShape(COLLECTION_NAME_1, shape)));
		assertEquals(new ShaclShape(COLLECTION_NAME_1, shape), service.retrieveShaclShape(COLLECTION_NAME_1));
	}

	@Test
	void when_collectionExists_and_hasNoShapeConfigured_then_retrieveRelatedShape() {
		when(repository.retrieveShaclShape(COLLECTION_NAME_1)).thenReturn(Optional.of(new ShaclShape(COLLECTION_NAME_1, null)));
		assertEquals(new ShaclShape(COLLECTION_NAME_1, null), service.retrieveShaclShape(COLLECTION_NAME_1));
	}

	@Test
	void when_collectionDoesNotExists_then_throwException() {
		when(repository.retrieveShaclShape(COLLECTION_NAME_1)).thenReturn(Optional.empty());
		Exception e = assertThrows(MissingShaclShapeException.class, () -> service.retrieveShaclShape(COLLECTION_NAME_1));
		assertEquals("No shacl shape configured for collection " + COLLECTION_NAME_1, e.getMessage());
	}

	@Test
	void when_collectionExists_and_updateShape_then_expectUpdatedShacl() throws URISyntaxException {
		final Model newShape = readModelFromFile("eventstream/streams/example-shape.ttl");
		final ShaclShape shaclShape = new ShaclShape(COLLECTION_NAME_1, newShape);

		when(repository.existByCollection(COLLECTION_NAME_1)).thenReturn(true);
		when(repository.saveShaclShape(shaclShape)).thenReturn(shaclShape);

		ShaclShape updateShaclShape = service.updateShaclShape(new ShaclShape(COLLECTION_NAME_1, newShape));

		assertEquals(shaclShape, updateShaclShape);
		verify(repository).saveShaclShape(shaclShape);
		verify(applicationEventPublisher).publishEvent(shaclChangedEventArgumentCaptor.capture());
		ShaclChangedEvent shaclChangedEvent = shaclChangedEventArgumentCaptor.getValue();
		assertEquals(shaclShape, shaclChangedEvent.getShacl());
	}

	@Test
	void when_collectionDoesNotExists_and_tryUpdateShape_then_throwException() throws URISyntaxException {
		final Model newShape = readModelFromFile("eventstream/streams/example-shape.ttl");
		final ShaclShape shaclShape = new ShaclShape(COLLECTION_NAME_1, newShape);

		when(repository.existByCollection(COLLECTION_NAME_1)).thenReturn(false);

		assertThrows(MissingShaclShapeException.class, () -> service.updateShaclShape(shaclShape));
		verify(repository).existByCollection(COLLECTION_NAME_1);
		verify(repository, never()).saveShaclShape(shaclShape);
		verifyNoInteractions(applicationEventPublisher);
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}
}
