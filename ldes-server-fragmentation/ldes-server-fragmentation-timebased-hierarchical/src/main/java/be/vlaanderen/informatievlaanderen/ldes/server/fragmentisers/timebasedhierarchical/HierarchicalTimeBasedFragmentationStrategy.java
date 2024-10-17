package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.LocalDateTimeConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services.TimeBasedBucketFinder;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

public class HierarchicalTimeBasedFragmentationStrategy extends FragmentationStrategyDecorator {

	public static final String TIMEBASED_FRAGMENTATION_HIERARCHICAL = "HierarchicalTimeBasedFragmentation";
	private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalTimeBasedFragmentationStrategy.class);

	private final ObservationRegistry observationRegistry;
	private final TimeBasedBucketFinder bucketFinder;
	private final TimeBasedConfig config;

	public HierarchicalTimeBasedFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
	                                                  ObservationRegistry observationRegistry,
	                                                  TimeBasedBucketFinder bucketFinder,
	                                                  TimeBasedConfig config) {
		super(fragmentationStrategy);
		this.observationRegistry = observationRegistry;
		this.bucketFinder = bucketFinder;
		this.config = config;
	}

	@Override
	public void addMemberToBucket(Bucket parentBucket, FragmentationMember member, Observation parentObservation) {
		final Observation bucketisationObservation = startFragmentationObservation(parentObservation);

		Bucket childBucket = getFragmentationTimestamp(member.getSubject(), member.getVersionModel())
				.map(timestamp -> bucketFinder.getLowestBucket(parentBucket, timestamp, Granularity.YEAR))
				.orElseGet(() -> bucketFinder.getDefaultFragment(parentBucket));

		super.addMemberToBucket(childBucket, member, parentObservation);
		bucketisationObservation.stop();
	}

	private Optional<FragmentationTimestamp> getFragmentationTimestamp(String subject, Model memberModel) {
		try {
			Optional<LocalDateTime> timeStamp = getFragmentationObjectLocalDateTime(memberModel,
					config.getFragmenterSubjectFilter(),
					config.getFragmentationPath());
			return timeStamp.map(localDateTime -> new FragmentationTimestamp(localDateTime, config.getMaxGranularity()));
		} catch (Exception exception) {
			LOGGER.warn("Could not fragment member: {} Reason: {}", subject, exception.getMessage());
			return Optional.empty();
		}
	}

	private Observation startFragmentationObservation(Observation parentObservation) {
		return Observation.createNotStarted("timebased fragmentation", observationRegistry)
				.parentObservation(parentObservation)
				.start();
	}

	private Optional<LocalDateTime> getFragmentationObjectLocalDateTime(Model model, String subjectFilter,
	                                                                    String fragmentationPath) {
		return model
				.listStatements(null, ResourceFactory.createProperty(fragmentationPath), (Resource) null)
				.toList()
				.stream()
				.filter(statement -> statement.getSubject().toString().matches(subjectFilter))
				.map(this::getDateTimeValue)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
	}

	private Optional<LocalDateTime> getDateTimeValue(Statement statement) {
		LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

		try {
			Literal literal = statement.getObject().asLiteral();
			return Optional.of(localDateTimeConverter.getLocalDateTime(literal));
		} catch (Exception exception) {
			LOGGER.warn("Could not extract datetime from: {} Reason: {}", statement, exception.getMessage());
			return Optional.empty();
		}
	}
}
