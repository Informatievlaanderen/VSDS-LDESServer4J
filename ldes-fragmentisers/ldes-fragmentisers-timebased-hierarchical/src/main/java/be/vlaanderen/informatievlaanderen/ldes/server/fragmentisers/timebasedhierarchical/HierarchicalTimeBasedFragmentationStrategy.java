package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.LocalDateTimeConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services.TimeBasedFragmentFinder;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class HierarchicalTimeBasedFragmentationStrategy extends FragmentationStrategyDecorator {

	public static final String TIMEBASED_FRAGMENTATION_HIERARCHICAL = "HierarchicalTimeBasedFragmentation";
	private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalTimeBasedFragmentationStrategy.class);

	private final ObservationRegistry observationRegistry;
	private final TimeBasedFragmentFinder fragmentFinder;
	private final TimeBasedConfig config;

	public HierarchicalTimeBasedFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			ObservationRegistry observationRegistry,
			TimeBasedFragmentFinder fragmentFinder,
			FragmentRepository fragmentRepository,
			TimeBasedConfig config) {
		super(fragmentationStrategy, fragmentRepository);
		this.observationRegistry = observationRegistry;
		this.fragmentFinder = fragmentFinder;
		this.config = config;
	}

	@Override
	public List<BucketisedMember> addMemberToFragment(Fragment parentFragment, FragmentationMember member,
													  Observation parentObservation) {
		final Observation fragmentationObservation = startFragmentationObservation(parentObservation);

		Fragment fragment = getFragmentationTimestamp(member.id(), member.model())
				.map(timestamp -> fragmentFinder.getLowestFragment(parentFragment, timestamp, Granularity.YEAR))
				.orElseGet(() -> fragmentFinder.getDefaultFragment(parentFragment));

		List<BucketisedMember> members = super.addMemberToFragment(fragment, member, fragmentationObservation);
		fragmentationObservation.stop();
		return members;
	}

	private Optional<FragmentationTimestamp> getFragmentationTimestamp(String memberId, Model memberModel) {
		try{
			Optional<LocalDateTime> timeStamp = getFragmentationObjectLocalDateTime(memberModel,
					config.getFragmenterSubjectFilter(),
					config.getFragmentationPath());
			return timeStamp.map(localDateTime -> new FragmentationTimestamp(localDateTime, config.getMaxGranularity()));
		} catch (Exception exception) {
			LOGGER.warn("Could not fragment member: {} Reason: {}", memberId, exception.getMessage());
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
