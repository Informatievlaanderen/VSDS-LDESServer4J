package be.vlaanderen.informatievlaanderen.ldes.server.domain.view;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InMemoryViewCollectionTest {

	private final ViewRepository viewRepository = mock(ViewRepository.class);
	private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
	private final ViewCollection viewCollection = new InMemoryViewCollection(viewRepository, eventPublisher);

	@Test
	void test_InsertionAndRetrieval() {
		ViewSpecification view = new ViewSpecification(new ViewName("collection", "view"),
				getRetentionPolicies(),
				getFragmentations());

		Optional<ViewSpecification> retrievedView = viewCollection.getViewByViewName(view.getName());
		assertFalse(retrievedView.isPresent());

		viewCollection.addView(view);

		verify(viewRepository).saveView(view);

		retrievedView = viewCollection.getViewByViewName(view.getName());
		assertTrue(retrievedView.isPresent());
		assertEquals(view, retrievedView.get());
	}

	private List<FragmentationConfig> getFragmentations() {
		FragmentationConfig fragmentationConfig = new FragmentationConfig();
		fragmentationConfig.setName("GeoSpatial");
		fragmentationConfig.setConfig(Map.of("ZoomLevel", "15"));
		return List.of(fragmentationConfig);
	}

	private List<RetentionConfig> getRetentionPolicies() {
		RetentionConfig retentionConfig = new RetentionConfig();
		retentionConfig.setName("VersionBased");
		retentionConfig.setConfig(Map.of("amount", "2"));
		return List.of(retentionConfig);
	}

}