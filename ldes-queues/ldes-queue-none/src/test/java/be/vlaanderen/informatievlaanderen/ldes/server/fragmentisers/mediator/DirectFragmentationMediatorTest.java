package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.mediator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmenters.mediator.DirectFragmentationMediator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DirectFragmentationMediatorTest {

	private FragmentationMediator fragmentationMediator;

	private final FragmentationExecutor fragmentationExecutor = mock(FragmentationExecutor.class);

	@BeforeEach
	void setUp() {
		fragmentationMediator = new DirectFragmentationMediator(fragmentationExecutor);
	}

	@Test
	@DisplayName("Adding a member to the queue")
	void when_MemberIsAddedForFragmentation_FragmentationExecutorIsCalled() {
		LdesMember ldesMember = mock(LdesMember.class);
		fragmentationMediator.addMemberToFragment(ldesMember);

		verify(fragmentationExecutor, times(1)).executeFragmentation(ldesMember);
	}

}