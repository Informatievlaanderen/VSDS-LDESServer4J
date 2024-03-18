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
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TimeBasedRelationsAttributerTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final FragmentPair timePair = new FragmentPair(Granularity.YEAR.getValue(), "2023");
	private static final FragmentPair monthPair = new FragmentPair(Granularity.MONTH.getValue(), "02");
	private Fragment parentFragment;
	private TimeBasedRelationsAttributer relationsAttributer;
	private FragmentRepository fragmentRepository;
	private TimeBasedConfig config;

	@BeforeEach
	void setUp() {
		parentFragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of(timePair)));
		fragmentRepository = mock(FragmentRepository.class);
		config = new TimeBasedConfig(".*", "", Granularity.SECOND, false);
		relationsAttributer = new TimeBasedRelationsAttributer(fragmentRepository, config);
	}

	@Test
	void when_RelationNotPresent_AndCachingDisabled_ThenRelationIsAdded_NextUpdateTsIsNotSet_ChildrenStayMutable() {
		Fragment child = parentFragment.createChild(monthPair);

		TreeRelation expected = new TreeRelation(config.getFragmentationPath(),
				child.getFragmentId(),
				LocalDateTime.of(2023,2,1,0,0).toString()
				, XSD_DATETIME, TREE_GTE_RELATION);

		relationsAttributer.addInBetweenRelation(parentFragment, child);

		assertThat(parentFragment.containsRelation(expected)).isTrue();
		verify(fragmentRepository, times(2)).saveFragment(parentFragment);
		assertThat(parentFragment.getNextUpdateTs()).isNull();
		verify(fragmentRepository, times(0)).makeChildrenImmutable(any());
	}

	@Test
	void when_RelationNotPresent_AndCachingEnabled_ThenRelationIsAdded_NextUpdateTsIsSet_AndChildrenBecomeImmutable() {
		config = new TimeBasedConfig(".*", "", Granularity.SECOND, true);
		relationsAttributer = new TimeBasedRelationsAttributer(fragmentRepository, config);

		Fragment child = parentFragment.createChild(monthPair);
		TreeRelation gteRelation = new TreeRelation(config.getFragmentationPath(),
				child.getFragmentId(),
				LocalDateTime.of(2023,2,1,0,0).toString()
				, XSD_DATETIME, TREE_GTE_RELATION);

		TreeRelation ltRelation = new TreeRelation(config.getFragmentationPath(),
				child.getFragmentId(),
				LocalDateTime.of(2023,3,1,0,0).toString()
				, XSD_DATETIME, TREE_LT_RELATION);

		relationsAttributer.addInBetweenRelation(parentFragment, child);

		assertThat(parentFragment.containsRelation(gteRelation)).isTrue();
		assertThat(parentFragment.containsRelation(ltRelation)).isTrue();
		verify(fragmentRepository, times(2)).saveFragment(parentFragment);
		assertThat(parentFragment.getNextUpdateTs()).isEqualTo(LocalDateTime.of(2023, 2, 28, 23, 59, 59));
		verify(fragmentRepository, times(2)).makeChildrenImmutable(parentFragment);
	}

	@Test
	void when_RelationNotPresent_Then_AddDefaultRelation() {
		Fragment child = parentFragment.createChild(monthPair);
		TreeRelation expected = new TreeRelation("",
				child.getFragmentId(), "", "",
				GENERIC_TREE_RELATION);

		relationsAttributer.addDefaultRelation(parentFragment, child);

		assertThat(parentFragment.containsRelation(expected)).isTrue();
		assertThat(parentFragment.getNextUpdateTs()).isNull();
		verify(fragmentRepository).saveFragment(parentFragment);
	}

}
