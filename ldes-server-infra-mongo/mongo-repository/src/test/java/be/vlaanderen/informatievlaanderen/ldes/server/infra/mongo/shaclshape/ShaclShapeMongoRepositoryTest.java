package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.shaclshape;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.shaclshape.entity.ShaclShapeEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.shaclshape.repository.ShaclShapeEntityRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShaclShapeMongoRepositoryTest {
	private static final String COLLECTION = "collection1";
	@Mock
	private ShaclShapeEntityRepository shaclShapeEntityRepository;

	private ShaclShapeRepository repository;

	@BeforeEach
	void setUp() {
		repository = new ShaclShapeMongoRepository(shaclShapeEntityRepository);
	}

	@Test
	void when_retrieveShacl_then_shaclIsReturned() throws URISyntaxException, IOException {
		final String shaclShapeString = loadShaclString();
		final ShaclShapeEntity shaclShapeEntity = new ShaclShapeEntity(COLLECTION, shaclShapeString);

		when(shaclShapeEntityRepository.findById(COLLECTION)).thenReturn(Optional.of(shaclShapeEntity));

		final Model expectedShaclModel = ModelFactory.createDefaultModel();
		final ShaclShape expectedShaclShape = new ShaclShape(COLLECTION, expectedShaclModel);

		Optional<ShaclShape> shaclShape = repository.retrieveShaclShape(COLLECTION);

		verify(shaclShapeEntityRepository).findById(COLLECTION);
		assertTrue(shaclShape.isPresent());
		assertEquals(expectedShaclShape, shaclShape.get());
	}

	@Test
	void when_retrieveNonExistingShacl_then_emptyOptionalIsReturned() {
		when(shaclShapeEntityRepository.findById(COLLECTION)).thenReturn(Optional.empty());

		Optional<ShaclShape> shaclShape = repository.retrieveShaclShape(COLLECTION);

		verify(shaclShapeEntityRepository).findById(COLLECTION);
		assertTrue(shaclShape.isEmpty());
	}

	@Test
	void when_retrieveAllShacls_then_listIsReturned() throws URISyntaxException, IOException {
		final String shaclShapeString = loadShaclString();
		when(shaclShapeEntityRepository.findAll())
				.thenReturn(List.of(
						new ShaclShapeEntity("c1", ""),
						new ShaclShapeEntity("c2", shaclShapeString)));

		final List<ShaclShape> expectedShapes = List.of(
				new ShaclShape("c1", ModelFactory.createDefaultModel()),
				new ShaclShape("c2", RdfModelConverter.fromString(shaclShapeString, Lang.TURTLE))
		);

		final List<ShaclShape> shaclShapes = repository.retrieveAllShaclShapes();

		verify(shaclShapeEntityRepository).findAll();
		assertEquals(expectedShapes, shaclShapes);
	}

	@Test
	void when_saveShacl_then_shaclIsReturned() throws URISyntaxException, IOException {
		final String shaclShapeString = loadShaclString();
		final Model shaclModel = RdfModelConverter.fromString(shaclShapeString, Lang.TURTLE);
		final ShaclShape shaclShape = new ShaclShape(COLLECTION, shaclModel);

		final ShaclShape savedShaclShape = repository.saveShaclShape(shaclShape);

		verify(shaclShapeEntityRepository).save(any(ShaclShapeEntity.class));
		assertEquals(shaclShape, savedShaclShape);
	}

	private String loadShaclString() throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		Path path = Paths.get(Objects.requireNonNull(classLoader.getResource("shacl/shacl-shape.ttl")).toURI());
		return Files.lines(path).collect(Collectors.joining());
	}

}