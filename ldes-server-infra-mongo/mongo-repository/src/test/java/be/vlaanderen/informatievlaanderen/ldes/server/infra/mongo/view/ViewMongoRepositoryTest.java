package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.repository.ViewEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ViewMongoRepositoryTest {

	private final ViewEntityRepository viewEntityRepository = mock(ViewEntityRepository.class);

	private ViewMongoRepository repository;

	@BeforeEach
	void setUp() {
		repository = new ViewMongoRepository(viewEntityRepository);
	}

	@Test
    void test_retrievingAllViews_AND_retrievingAllViewsOfCollection() {
        when(viewEntityRepository.findAll())
                .thenReturn(List.of(
                        new ViewEntity("collection1/view1", List.of(), List.of()),
                        new ViewEntity("collection1/view2", List.of(), List.of()),
                        new ViewEntity("collection2/view1", List.of(), List.of())));

        final List<ViewSpecification> expectedViews = List.of(
                new ViewSpecification(new ViewName("collection1", "view1"), List.of(), List.of()),
                new ViewSpecification(new ViewName("collection1", "view2"), List.of(), List.of()),
                new ViewSpecification(new ViewName("collection2", "view1"), List.of(), List.of()));
        final List<ViewSpecification> viewsOfCollection1 = List.of(
                new ViewSpecification(new ViewName("collection1", "view1"), List.of(), List.of()),
                new ViewSpecification(new ViewName("collection1", "view2"), List.of(), List.of())
        );
        final List<ViewSpecification> viewsOfCollection2 = List.of(
                new ViewSpecification(new ViewName("collection2", "view1"), List.of(), List.of()));

        List<ViewSpecification> viewSpecifications = repository.retrieveAllViews();
        assertEquals(expectedViews, viewSpecifications);

        viewSpecifications = repository.retrieveAllViewsOfCollection("collection1");
        assertEquals(viewsOfCollection1, viewSpecifications);

        viewSpecifications = repository.retrieveAllViewsOfCollection("collection2");
        assertEquals(viewsOfCollection2, viewSpecifications);

        verify(viewEntityRepository, times(3)).findAll();
    }

	@Test
	void test_savingOfView() {
		final ViewSpecification view = new ViewSpecification(new ViewName("collection1", "view1"), List.of(),
				List.of());

		repository.saveView(view);

		verify(viewEntityRepository).save(any(ViewEntity.class));
	}

	@Test
	void test_deletingOfView() {
		final ViewName viewName = new ViewName("collection1", "view1");

		repository.deleteViewByViewName(viewName);

		verify(viewEntityRepository).deleteById(viewName.asString());
	}

	@Test
	void test_getViewByViewName() {
		ViewEntity viewEntity = new ViewEntity("collection1/view1", List.of(), List.of());
		ViewSpecification expectedViewSpecification = new ViewSpecification(new ViewName("collection1", "view1"),
				List.of(), List.of());
		when(viewEntityRepository.findById(expectedViewSpecification.getName().asString()))
				.thenReturn(Optional.of(viewEntity));

		Optional<ViewSpecification> actualViewSpecification = repository
				.getViewByViewName(expectedViewSpecification.getName());

		verify(viewEntityRepository).findById(expectedViewSpecification.getName().asString());
		assertTrue(actualViewSpecification.isPresent());
		assertEquals(expectedViewSpecification, actualViewSpecification.get());
	}

}
