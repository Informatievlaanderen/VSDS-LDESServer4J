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

import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

class TimeBasedRelationsAttributerTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final BucketDescriptorPair timePair = new BucketDescriptorPair(Granularity.YEAR.getValue(), "2023");
	private static final BucketDescriptorPair monthPair = new BucketDescriptorPair(Granularity.MONTH.getValue(), "02");
	private Bucket parentBucket;
	private TimeBasedRelationsAttributer relationsAttributer;
	private TimeBasedConfig config;

	@BeforeEach
	void setUp() {
		config = new TimeBasedConfig(".*", "", Granularity.SECOND);
		relationsAttributer = new TimeBasedRelationsAttributer(config);
		parentBucket = new Bucket(BucketDescriptor.of(timePair), VIEW_NAME);
	}

	@Test
	void when_RelationNotPresent_ThenRelationIsAdded_NextUpdateTsIsNotSet_ChildrenStayMutable() {
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
						child.withRelations(gteRelation, ltRelation)
				);

	}

	@Test
	void when_RelationNotPresent_Then_AddDefaultRelation() {
		Bucket child = parentBucket.createChild(monthPair);

		relationsAttributer.addDefaultRelation(parentBucket, child);

		assertThat(parentBucket.getChildren())
				.containsExactly(child.withGenericRelation());
	}

}
