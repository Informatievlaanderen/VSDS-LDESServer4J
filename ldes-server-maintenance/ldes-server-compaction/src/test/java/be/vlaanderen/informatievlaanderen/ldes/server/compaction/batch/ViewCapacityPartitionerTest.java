package be.vlaanderen.informatievlaanderen.ldes.server.compaction.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ExecutionContext;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViewCapacityPartitionerTest {
	private static final String COLLECTION = "collection";
	private static final ViewName VIEW_A = new ViewName(COLLECTION, "viewA");
	private static final ViewName VIEW_B = new ViewName(COLLECTION, "viewB");
	private static final ViewName VIEW_C = new ViewName(COLLECTION, "viewC");
	@Mock
	private ViewCollection viewCollection;
	@InjectMocks
	private ViewCapacityPartitioner viewCapacityPartitioner;

	@Test
	void given_NonEmptyCollection_testPartitioning() {
		int capacityA = 20;
		int capacityB = 40;
		int capacityC = 60;


		when(viewCollection.getAllViewCapacities()).thenReturn(Set.of(
				new ViewCapacity(VIEW_A, capacityA),
				new ViewCapacity(VIEW_B, capacityB),
				new ViewCapacity(VIEW_C, capacityC)
		));

		final Map<String, ExecutionContext> result = viewCapacityPartitioner.partition(0);

		assertThat(result)
				.hasSize(3)
				.containsAllEntriesOf(Map.of(
						"view:%s".formatted(VIEW_A.asString()),
						new ExecutionContext(Map.of("viewName", VIEW_A.asString(), "capacityPerPage", capacityA)),
						"view:%s".formatted(VIEW_B.asString()),
						new ExecutionContext(Map.of("viewName", VIEW_B.asString(), "capacityPerPage", capacityB)),
						"view:%s".formatted(VIEW_C.asString()),
						new ExecutionContext(Map.of("viewName", VIEW_C.asString(), "capacityPerPage", capacityC))
				));
	}
}