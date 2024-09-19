package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TimeBasedRelationsAttributerTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final BucketDescriptorPair timePair = new BucketDescriptorPair(Granularity.YEAR.getValue(), "2023");
	private static final BucketDescriptorPair monthPair = new BucketDescriptorPair(Granularity.MONTH.getValue(), "02");
	private Bucket parentBucket;
	private TimeBasedRelationsAttributer relationsAttributer;
	private ApplicationEventPublisher applicationEventPublisher;
	private TimeBasedConfig config;

	@BeforeEach
	void setUp() {
		applicationEventPublisher = mock(ApplicationEventPublisher.class);
		config = new TimeBasedConfig(".*", "", Granularity.SECOND, false);
		relationsAttributer = new TimeBasedRelationsAttributer(applicationEventPublisher, config);
		parentBucket = new Bucket(BucketDescriptor.of(timePair), VIEW_NAME);
	}

	@Test
	void when_RelationNotPresent_AndCachingDisabled_ThenRelationIsAdded_NextUpdateTsIsNotSet_ChildrenStayMutable() {
		Bucket child = parentBucket.createChild(monthPair);

		TreeRelation gteRelation = new TreeRelation(
				TREE_GTE_RELATION,
				LocalDateTime.of(2023,2,1,0,0).toString(),
				XSD_DATETIME,
				config.getFragmentationPath());
		TreeRelation ltRelation = new TreeRelation(
				TREE_LT_RELATION,
				LocalDateTime.of(2023,3,1,0,0).toString(),
				XSD_DATETIME,
				config.getFragmentationPath());

		relationsAttributer.addInBetweenRelation(parentBucket, child);

		assertThat(parentBucket.getChildren())
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrder(
						child.withRelations(gteRelation),
						child.withRelations(ltRelation)
				);

	}

	@Test
	void when_RelationNotPresent_AndCachingEnabled_ThenRelationIsAdded_NextUpdateTsIsSet_AndChildrenBecomeImmutable() {
		config = new TimeBasedConfig(".*", "", Granularity.SECOND, true);
		relationsAttributer = new TimeBasedRelationsAttributer(applicationEventPublisher, config);
		Bucket child = parentBucket.createChild(monthPair);

		TreeRelation gteRelation = new TreeRelation(
				TREE_GTE_RELATION,
				LocalDateTime.of(2023,2,1,0,0).toString(),
				XSD_DATETIME,
				config.getFragmentationPath());
		TreeRelation ltRelation = new TreeRelation(
				TREE_LT_RELATION,
				LocalDateTime.of(2023,3,1,0,0).toString(),
				XSD_DATETIME,
				config.getFragmentationPath());

		relationsAttributer.addInBetweenRelation(parentBucket, child);

		assertThat(parentBucket.getChildren())
				.first()
				.usingRecursiveComparison()
				.isEqualTo(child.withRelations(gteRelation, ltRelation));
	}

	@Test
	void when_RelationNotPresent_Then_AddDefaultRelation() {
		Bucket child = parentBucket.createChild(monthPair);

		relationsAttributer.addDefaultRelation(parentBucket, child);

		assertThat(parentBucket.getChildren())
				.containsExactly(child.withGenericRelation());
	}

}
