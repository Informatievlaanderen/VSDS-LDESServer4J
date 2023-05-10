package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.repository.ViewEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void test_retrievingAllViews() {
        when(viewEntityRepository.findAll())
                .thenReturn(List.of(
                        new ViewEntity("collection1/view1", List.of(), List.of()),
                        new ViewEntity("collection1/view2", List.of(), List.of()),
                        new ViewEntity("collection2/view1", List.of(), List.of())));

        final List<ViewSpecification> expectedViews = List.of(
                new ViewSpecification(new ViewName("collection1","view1"), List.of(), List.of()),
                new ViewSpecification(new ViewName("collection1","view2"), List.of(), List.of()),
                new ViewSpecification(new ViewName("collection2","view1"), List.of(), List.of()));

        final List<ViewSpecification> viewSpecifications = repository.retrieveAllViews();

        verify(viewEntityRepository).findAll();
        assertEquals(expectedViews, viewSpecifications);
    }

	@Test
	void test_savingOfView() {
		final ViewSpecification view = new ViewSpecification(new ViewName("collection1", "view1"), List.of(),
				List.of());

		repository.saveView(view);

		verify(viewEntityRepository).save(any(ViewEntity.class));
	}

	@Test
	void test_deleteingOfView() {
		final ViewName viewName = new ViewName("collection1", "view1");

		repository.deleteViewByViewName(viewName);

		verify(viewEntityRepository).deleteById(viewName.asString());
	}

}