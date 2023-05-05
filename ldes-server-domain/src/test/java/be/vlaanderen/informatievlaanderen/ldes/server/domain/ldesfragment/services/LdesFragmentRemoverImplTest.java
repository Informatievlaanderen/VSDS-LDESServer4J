package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LdesFragmentRemoverImplTest {

	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);

	private final LdesFragmentRemover ldesFragmentRemover = new LdesFragmentRemoverImpl(ldesFragmentRepository);

	@Test
	void when_LdesFragmentOfViewAreRemoved_TheyAreRemovedFromRepository() {
		ViewName viewName = new ViewName("collection", "view");

		ldesFragmentRemover.removeLdesFragmentsOfView(viewName);

		verify(ldesFragmentRepository).removeLdesFragmentsOfView(viewName.asString());
	}

}