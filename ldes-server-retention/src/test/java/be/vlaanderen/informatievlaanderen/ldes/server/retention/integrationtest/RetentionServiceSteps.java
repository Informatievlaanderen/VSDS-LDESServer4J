package be.vlaanderen.informatievlaanderen.ldes.server.retention.integrationtest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RetentionServiceSteps extends RetentionIntegrationTest {

	private final RetentionConfigExtractor retentionConfigExtractor = new RetentionConfigExtractor();
	public static final String MEMBER_TEMPLATE_FILENAME = "features/data/memberTemplate.ttl";

	@DataTableType
	public EventStream EventStreamEntryTransformer(Map<String, String> row) {
		return new EventStream(
				row.get("collection"),
				row.get("timestampPath"),
				row.get("versionOfPath")
        );
	}

	@DataTableType
	public ViewSpecification ViewSpecificationEntryTransformer(Map<String, String> row) throws URISyntaxException {
		return new ViewSpecification(
				ViewName.fromString(row.get("viewName")),
				retentionConfigExtractor
						.readRetentionPolicyFromFile(row.get("rdfDescriptionFileName")), List.of(), 100);
	}

	@DataTableType
	public MemberIngestedEvent MemberIngestedEventEntryTransformer(Map<String, String> row)
			throws URISyntaxException, IOException {
		return new MemberIngestedEvent(
				createModel(row.get("versionOf"), row.get("timestamp")),
				row.get("id"),
				row.get("collectionName"), Integer.parseInt(row.get("sequenceNumber")));
	}

	@Given("an EventStream with the following properties")
	public void anEventStreamWithTheFollowingProperties(EventStream eventStream) {
		applicationEventPublisher.publishEvent(new EventStreamCreatedEvent(eventStream));
	}

	@And("the following Members are ingested")
	public void theFollowingMembersAreIngested(List<MemberIngestedEvent> ingestedMembers) {
		ingestedMembers.forEach(applicationEventPublisher::publishEvent);
	}

	@When("a view with the following properties is created")
	public void aViewWithTheFollowingPropertiesIsCreated(ViewSpecification viewSpecification) {
		ViewAddedEvent viewAddedEvent = new ViewAddedEvent(viewSpecification);
		applicationEventPublisher.publishEvent(viewAddedEvent);
	}

	@When("wait for {int} seconds until the scheduler has executed at least once")
	public void wait_for_seconds_until_the_scheduler_has_executed_at_least_once(Integer secondsToWait) {
		await()
				.timeout(secondsToWait + 1, SECONDS)
				.pollDelay(secondsToWait, SECONDS)
				.untilAsserted(() -> assertTrue(true));
	}

	private Model createModel(String versionOf, String timestamp) throws URISyntaxException, IOException {
		String modelTemplate = readMemberTemplateFromFile();
		String updatedModel = modelTemplate.replace("#VERSIONOF", versionOf).replace("#TIMESTAMP", timestamp);
		return RDFParserBuilder.create()
				.fromString(updatedModel).lang(Lang.TURTLE)
				.toModel();
	}

	private String readMemberTemplateFromFile() throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		URI uri = Objects.requireNonNull(classLoader.getResource(MEMBER_TEMPLATE_FILENAME)).toURI();
		return Files.lines(Paths.get(uri)).collect(Collectors.joining());
	}

	@And("the following members are allocated to the view {string}")
	public void theFollowingMembersAreAllocatedToTheView(String viewName, List<String> members) {
		members.forEach(member -> applicationEventPublisher.publishEvent(new MemberAllocatedEvent(member,
				ViewName.fromString(viewName).getCollectionName(), ViewName.fromString(viewName).getViewName(), "")));
	}

	@Then("the view {string} only contains following members")
	public void theViewOnlyContainsFollowingMembers(String viewName, List<String> memberIds) {
		// Note: it's difficult to capture the MemberUnallocatedEvents and
		// MemberDeletedEvents, since these are executed in a different thread due to
		// the Scheduling. The ApplicationEventsApplicationListener from
		// spring-boot-test is not registered to this thread. Hence, these events do not
		// pop up in for example ApplicationEvents from spring-boot-test. Therefore, we
		// use the repository to verify on existence of the members.
		Stream<String> members = memberPropertiesRepository.getMemberPropertiesWithViewReference(ViewName.fromString(viewName))
				.map(MemberProperties::getId);

		assertThat(members).containsExactlyInAnyOrder(memberIds.toArray(String[]::new));
	}
}
