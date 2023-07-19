package be.vlaanderen.informatievlaanderen.ldes.server.retention.integrationtest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RetentionServiceSteps extends RetentionIntegrationTest {

	public static final String MEMBER_TEMPLATE_FILENAME = "features/data/memberTemplate.ttl";

	@DataTableType
	public EventStream EventStreamEntryTransformer(Map<String, String> row) {
		return new EventStream(
				row.get("collection"),
				row.get("timestampPath"),
				row.get("versionOfPath"),
				row.get("memberType"));
	}

	@DataTableType
	public DeletedMember DeletedMemberEntryTransformer(Map<String, String> row) {
		return new DeletedMember(
				row.get("id"),
				Boolean.parseBoolean(row.get("deleted")));
	}

	@DataTableType
	public ViewSpecification ViewSpecificationEntryTransformer(Map<String, String> row) throws URISyntaxException {
		return new ViewSpecification(
				ViewName.fromString(row.get("viewName")),
				List.of(readRetentionPolicyFromFile(row.get("rdfDescriptionFileName"))), List.of());
	}

	@DataTableType
	public MemberIngestedEvent MemberIngestedEventEntryTransformer(Map<String, String> row)
			throws URISyntaxException, IOException {
		return new MemberIngestedEvent(
				createModel(row.get("versionOf"), row.get("timestamp")),
				row.get("id"),
				row.get("collectionName"));
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

	@Then("the following members are deleted")
	public void the_following_members_are_deleted(List<DeletedMember> deletedMembers) {
		// Note: it's difficult to capture the MemberUnallocatedEvents and
		// MemberDeletedEvents, since these are executed in a different thread due to
		// the Scheduling. The ApplicationEventsApplicationListener from
		// spring-boot-test is not registered to this thread. Hence, these events do not
		// pop up in for example ApplicationEvents from spring-boot-test. Therefore, we
		// use the repository to verify on existence of the members.
		deletedMembers.forEach(deletedMember -> {
			if (deletedMember.deleted) {
				assertTrue(memberPropertiesRepository.retrieve(deletedMember.id).isEmpty());
			} else {
				assertTrue(memberPropertiesRepository.retrieve(deletedMember.id).isPresent());
			}
		});
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

	private Model readRetentionPolicyFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}

	@And("the following members are allocated to the view {string}")
	public void theFollowingMembersAreAllocatedToTheView(String viewName, List<String> members) {
		members.forEach(member -> {
			applicationEventPublisher.publishEvent(new MemberAllocatedEvent(member, ViewName.fromString(viewName)));
		});
	}

	private record DeletedMember(String id, boolean deleted) {
	}
}
