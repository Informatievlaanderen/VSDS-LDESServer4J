package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MemberToFragmentRepository;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FragmentationServiceTest {

	@Mock
	private FragmentationStrategyCollection fragmentationStrategyCollection;

	@Mock
	private MemberToFragmentRepository memberToFragmentRepository;

	@InjectMocks
	private FragmentationService fragmentationService;

	@Test
	void when_MemberIngestedEvent_then_AllFragmentationExecutorsFromThisCollection_should_BeTriggered() {
		final MemberIngestedEvent memberIngestedEvent = new MemberIngestedEvent(ModelFactory.createDefaultModel(), "id",
				"collectionName", 1L);
		final FragmentationStrategyExecutor executorA = mock(FragmentationStrategyExecutor.class);
		final FragmentationStrategyExecutor executorB = mock(FragmentationStrategyExecutor.class);
		when(fragmentationStrategyCollection.getFragmentationStrategyExecutors(memberIngestedEvent.collectionName()))
				.thenReturn(List.of(executorA, executorB));
		List<ViewName> views = List.of(ViewName.fromString("c/v1"), ViewName.fromString("c/v2"));
		when(fragmentationStrategyCollection.getViews(memberIngestedEvent.collectionName()))
				.thenReturn(views);

		fragmentationService.executeFragmentation(memberIngestedEvent);

		InOrder inOrder = inOrder(memberToFragmentRepository, executorA, executorB);
		final Member member = new Member(memberIngestedEvent.id(), memberIngestedEvent.model(), memberIngestedEvent.sequenceNr());
		inOrder.verify(memberToFragmentRepository).create(views, member);
		inOrder.verify(executorA).executeNext();
		inOrder.verify(executorB).executeNext();
	}

}
