package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.ViewNeedsRebucketisationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.ViewBucketisationService.ServiceType.FRAGMENTATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.ViewBucketisationService.ServiceType.PAGINATION;
import static org.mockito.Mockito.*;

class ViewBucketisationServiceTest {
	private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);

	private final String collection = "es";
	private final ViewName viewName = new ViewName(collection, "v");

	@Test
	void testTrigger_Pagination() {
		ViewBucketisationService service = new ViewBucketisationService(eventPublisher);

		service.setHasView(viewName, PAGINATION);
		verifyNoInteractions(eventPublisher);

		service.setHasView(viewName, FRAGMENTATION);
		verify(eventPublisher).publishEvent(any(ViewNeedsRebucketisationEvent.class));
	}

	@Test
	void testTrigger_viewDeletion() {
		ViewBucketisationService service = new ViewBucketisationService(eventPublisher);

		service.setHasView(viewName, PAGINATION);
		verifyNoInteractions(eventPublisher);
		service.setDeletedView(viewName, PAGINATION);

		service.setHasView(viewName, FRAGMENTATION);
		verifyNoInteractions(eventPublisher);
	}

	@Test
	void testTrigger_collectionDeletion() {
		ViewBucketisationService service = new ViewBucketisationService(eventPublisher);

		service.setHasView(viewName, PAGINATION);
		verifyNoInteractions(eventPublisher);
		service.setDeletedCollection(collection, PAGINATION);

		service.setHasView(viewName, FRAGMENTATION);
		verifyNoInteractions(eventPublisher);
	}
}