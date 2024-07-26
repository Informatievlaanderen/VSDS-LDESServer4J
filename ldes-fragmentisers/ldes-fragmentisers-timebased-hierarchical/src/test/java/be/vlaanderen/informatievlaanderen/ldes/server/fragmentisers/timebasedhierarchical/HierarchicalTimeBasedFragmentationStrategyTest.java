package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.EventStreamProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services.TimeBasedBucketFinder;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

import static org.apache.jena.riot.RDFDataMgr.loadModel;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class HierarchicalTimeBasedFragmentationStrategyTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final Bucket PARENT_BUCKET = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
	private static final Bucket CHILD_BUCKET = new Bucket(new BucketDescriptor(List.of(new BucketDescriptorPair("is", "child"))), VIEW_NAME);
	private static final LocalDateTime TIME = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
	private static final Granularity GRANULARITY = Granularity.SECOND;
	private static final EventStreamProperties EVENT_STREAM_PROPERTIES = new EventStreamProperties("collectionName", "versionOf", "timestampPath", false);
	private HierarchicalTimeBasedFragmentationStrategy fragmentationStrategy;
	private FragmentationStrategy decoratedFragmentationStrategy;
	private TimeBasedBucketFinder bucketFinder;

	@BeforeEach
	void setUp() {
		TimeBasedConfig config = new TimeBasedConfig(".*", "http://purl.org/dc/terms/created", GRANULARITY, false);
		bucketFinder = mock(TimeBasedBucketFinder.class);
		decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
		fragmentationStrategy = new HierarchicalTimeBasedFragmentationStrategy(decoratedFragmentationStrategy,
				ObservationRegistry.create(), bucketFinder, mock(), config);
	}

	@Test
	void when_FragmentationCalled_Then_FunctionsAreCalled() {
		Model model = loadModel("member_with_created_property.nq");
		FragmentationMember fragmentationMember = new FragmentationMember(1, "subject", "versionOf", TIME, EVENT_STREAM_PROPERTIES, model);
		FragmentationTimestamp fragmentationTimestamp = new FragmentationTimestamp(TIME, GRANULARITY);
		when(bucketFinder.getLowestBucket(PARENT_BUCKET, fragmentationTimestamp, Granularity.YEAR))
				.thenReturn(CHILD_BUCKET);

		fragmentationStrategy.addMemberToBucket(PARENT_BUCKET, fragmentationMember, mock());

		InOrder inOrder = Mockito.inOrder(bucketFinder, decoratedFragmentationStrategy);
		inOrder.verify(bucketFinder).getLowestBucket(PARENT_BUCKET, fragmentationTimestamp, Granularity.YEAR);
		inOrder.verify(decoratedFragmentationStrategy).addMemberToBucket(eq(CHILD_BUCKET), any(), any());
	}

	@Test
	void when_BucketisationCalled_Then_FunctionsAreCalled() {
		Model model = loadModel("member_with_created_property.nq");
		FragmentationMember member = new FragmentationMember(1, "subject", "versionOf", TIME, EVENT_STREAM_PROPERTIES, model);
		FragmentationTimestamp fragmentationTimestamp = new FragmentationTimestamp(TIME, GRANULARITY);
		when(bucketFinder.getLowestBucket(PARENT_BUCKET, fragmentationTimestamp, Granularity.YEAR))
				.thenReturn(CHILD_BUCKET);

		fragmentationStrategy.addMemberToBucket(PARENT_BUCKET, member, mock(Observation.class));

		InOrder inOrder = Mockito.inOrder(bucketFinder, decoratedFragmentationStrategy);
		inOrder.verify(bucketFinder).getLowestBucket(PARENT_BUCKET, fragmentationTimestamp, Granularity.YEAR);
		inOrder.verify(decoratedFragmentationStrategy,
				times(1)).addMemberToBucket(eq(CHILD_BUCKET), any(),
				any(Observation.class));
	}

	@Test
	void when_FragmentationCalledForMemberWithMissingTimestamp_Then_FunctionsAreCalled() {
		FragmentationMember member = mock(FragmentationMember.class);
		when(bucketFinder.getDefaultFragment(PARENT_BUCKET)).thenReturn(CHILD_BUCKET);

		fragmentationStrategy.addMemberToBucket(PARENT_BUCKET, member, mock());

		InOrder inOrder = Mockito.inOrder(bucketFinder, decoratedFragmentationStrategy);
		inOrder.verify(bucketFinder).getDefaultFragment(PARENT_BUCKET);
		inOrder.verify(decoratedFragmentationStrategy).addMemberToBucket(eq(CHILD_BUCKET), any(), any());
	}

	@Test
	void when_BucketisationCalledForMemberWithMissingTimestamp_Then_FunctionsAreCalled() {
		FragmentationMember member = mock(FragmentationMember.class);
		when(bucketFinder.getDefaultFragment(PARENT_BUCKET))
				.thenReturn(CHILD_BUCKET);

		fragmentationStrategy.addMemberToBucket(PARENT_BUCKET, member,
				mock(Observation.class));

		InOrder inOrder = Mockito.inOrder(bucketFinder, decoratedFragmentationStrategy);
		inOrder.verify(bucketFinder).getDefaultFragment(PARENT_BUCKET);
		inOrder.verify(decoratedFragmentationStrategy,
				times(1)).addMemberToBucket(eq(CHILD_BUCKET), any(),
				any(Observation.class));
	}

}
