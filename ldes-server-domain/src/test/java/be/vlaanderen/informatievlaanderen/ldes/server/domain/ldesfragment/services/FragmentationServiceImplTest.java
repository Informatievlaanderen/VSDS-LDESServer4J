package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FragmentationServiceImplTest {
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final LdesConfig ldesConfig = mock(LdesConfig.class);
	private final Tracer tracer = mock(Tracer.class);

	private final FragmentationServiceImpl fragmentationService = new FragmentationServiceImpl(ldesFragmentRepository,
			ldesConfig, "view", tracer);

	@Test
	void when_memberIsAddedToFragment_FragmentationServiceImplSavesUpdatedFragment() {
		LdesFragment ldesFragment = new LdesFragment("fragmentId", new FragmentInfo("view", List.of()));
		LdesMember ldesMember = mock(LdesMember.class);
		when(ldesMember.getLdesMemberId()).thenReturn("memberId");
		Span parentSpan = mock(Span.class);
		Span childSpan = mock(Span.class);
		when(tracer.nextSpan(parentSpan)).thenReturn(childSpan);
		when(childSpan.name("add Member to fragment")).thenReturn(childSpan);
		when(childSpan.start()).thenReturn(childSpan);

		fragmentationService.addMemberToFragment(ldesFragment, ldesMember, parentSpan);

		verify(tracer, times(1)).nextSpan(parentSpan);
		verify(ldesFragmentRepository, times(1)).saveFragment(ldesFragment);
		assertEquals(List.of("memberId"), ldesFragment.getMemberIds());
		verify(childSpan, times(1)).start();
		verify(childSpan, times(1)).end();
	}
}