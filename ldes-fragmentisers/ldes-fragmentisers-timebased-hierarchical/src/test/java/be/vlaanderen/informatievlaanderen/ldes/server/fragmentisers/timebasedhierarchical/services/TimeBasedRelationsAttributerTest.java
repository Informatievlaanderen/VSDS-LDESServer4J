package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.TREE_INBETWEEN_RELATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TimeBasedRelationsAttributerTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final FragmentPair timePair = new FragmentPair(Granularity.YEAR.getValue(), "2023");
	private static final FragmentPair monthPair = new FragmentPair(Granularity.MONTH.getValue(), "02");
	private static final Fragment PARENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of(timePair)));
	private TimeBasedRelationsAttributer relationsAttributer;
	private FragmentRepository fragmentRepository;
	private TimeBasedConfig config;

	@BeforeEach
	void setUp() {
		fragmentRepository = mock(FragmentRepository.class);
		config = new TimeBasedConfig(".*", "", Granularity.SECOND);
		relationsAttributer = new TimeBasedRelationsAttributer(fragmentRepository, config);
	}

	@Test
	void when_RelationNotPresent_Then_AddRelation() {
		Fragment child = PARENT.createChild(monthPair);
		TreeRelation expected = new TreeRelation(config.getFragmentationPath(),
				child.getFragmentId(),
				"2023-02", Granularity.MONTH.getType(),
				TREE_INBETWEEN_RELATION);

		relationsAttributer.addInBetweenRelation(PARENT, child);

		assertThat(PARENT.containsRelation(expected)).isTrue();
		assertThat(PARENT.getNextUpdateTs()).isEqualTo(LocalDateTime.of(2023, 2, 28, 23, 59, 59));
		verify(fragmentRepository).saveFragment(PARENT);
	}


	@Test
	void when_RelationNotPresent_Then_AddDefaultRelation() {
		Fragment child = PARENT.createChild(monthPair);
		TreeRelation expected = new TreeRelation("",
				child.getFragmentId(), "", "",
				GENERIC_TREE_RELATION);

		relationsAttributer.addDefaultRelation(PARENT, child);

		assertThat(PARENT.containsRelation(expected)).isTrue();
		assertThat(PARENT.getNextUpdateTs()).isNull();
		verify(fragmentRepository).saveFragment(PARENT);
	}

}
