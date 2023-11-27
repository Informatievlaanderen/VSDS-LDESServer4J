package be.vlaanderen.informatievlaanderen.ldes.server.retention;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MemberPropertiesRepositorySteps extends MongoRetentionIntegrationTest {

	private List<MemberProperties> memberProperties;
	private List<MemberProperties> retrievedMemberProperties;

	@DataTableType
	public MemberProperties memberPropertiesEntryTransformer(Map<String, String> row) {
		MemberProperties properties = new MemberProperties(
				row.get("id"),
				row.get("collectionName"),
				row.get("versionOf"),
				LocalDateTime.parse(row.get("timestamp")));
		properties.addViewReference(row.get("viewReference"));
		return properties;
	}

	@Before
	public void initialization() {
		retrievedMemberProperties = List.of();
		memberPropertiesRepository.removeMemberPropertiesOfCollection("mobility-hindrances");
	}

	@Given("The following MemberProperties")
	public void theFollowingMemberProperties(List<MemberProperties> memberProperties) {
		this.memberProperties = memberProperties;
	}

	@When("I save the MemberProperties using the MemberPropertiesRepository")
	public void iSaveTheMemberPropertiesUsingTheMemberPropertiesRepository() {
		memberProperties.forEach(memberPropertiesRepository::save);
	}

	@When("I save the MemberProperties without view using the MemberPropertiesRepository")
	public void iSaveTheMemberPropertiesWithoutViewUsingTheMemberPropertiesRepository() {
		memberProperties.forEach(memberPropertiesRepository::saveMemberPropertiesWithoutViews);
	}

	@And("I add the view with name {string} to the MemberProperties with id {string}")
	public void addViewToMember(String viewName, String memberId) {
		memberPropertiesRepository.addViewReference(memberId, viewName);
	}

	@And("I retrieve the MemberProperties with id {string}")
	public void iRetrieveTheMemberPropertiesWithId(String id) {
		retrievedMemberProperties = memberPropertiesRepository.retrieve(id).stream().toList();
	}

	@And("The retrieved MemberProperties has the view {string} as a property")
	public void theMemberPropertyContainsTheView(String view) {
		assertTrue(retrievedMemberProperties.get(0).containsViewReference(view));
	}

	@And("I retrieve all MemberProperties with versionOf {string} from view {string}")
	public void iRetrieveAllMemberPropertiesWithVersionOf(String versionOf, String viewName) {
		retrievedMemberProperties = memberPropertiesRepository.getMemberPropertiesOfVersionAndView(versionOf, viewName);
	}

	@Then("I have retrieved {int} MemberProperties")
	public void iHaveRetrievedMemberProperties(int numberOfRetrievedProperties) {
		assertEquals(numberOfRetrievedProperties, retrievedMemberProperties.size());
	}

	@And("The retrieved MemberProperties contains MemberProperties {int} of the table")
	public void theRetrievedMemberPropertiesContainsMemberPropertiesOfTheTable(int index) {
		assertTrue(retrievedMemberProperties
				.stream()
				.map(MemberProperties::getId)
				.anyMatch(id -> id.equals(memberProperties.get(index - 1).getId())));
	}

	@And("The retrieved MemberProperties contains MemberProperties with id {string}")
	public void theRetrievedMemberPropertiesContainsMemberPropertiesOfTheTable(String expectedId) {
		assertTrue(retrievedMemberProperties
				.stream()
				.map(MemberProperties::getId)
				.anyMatch(id -> id.equals(expectedId)));
	}

	@And("I retrieve all MemberProperties with view {string}")
	public void iRetrieveAllMemberPropertiesWithView(String viewNameString) {
		ViewName viewName = ViewName.fromString(viewNameString);
		retrievedMemberProperties = memberPropertiesRepository.getMemberPropertiesWithViewReference(viewName).toList();
	}

	@And("I remove the view with name {string} of the MemberProperties with id {string}")
	public void iRemoveTheViewWithNameOfTheMemberPropertiesWithId(String viewName, String id) {
		memberPropertiesRepository.removeViewReference(id, viewName);
	}

	@And("I remove the eventStream with name {string}")
	public void iRemoveTheEventStreamWithName(String collectionName) {
		memberPropertiesRepository.removeMemberPropertiesOfCollection(collectionName);
	}

	@And("The retrieved MemberProperties does not have the view {string} as a property")
	public void theRetrievedMemberPropertiesDoesNotHaveTheViewAsAProperty(String viewName) {
		assertFalse(retrievedMemberProperties.get(0).containsViewReference(viewName));
	}

	@And("I delete the MemberProperties with id {string}")
	public void iDeleteTheMemberPropertiesWithId(String id) {
		memberPropertiesRepository.deleteById(id);
	}

	@And("I retrieve the expired MemberProperties for {string} using TimeBasedRetentionPolicy with duration {string}")
	public void iRetrieveTheExpiredMemberPropertiesForUsingTimeBasedRetentionPolicyWithDuration(String viewNameString,
																								String durationString) {
		Duration duration = Duration.parse(durationString);
		ViewName viewName = ViewName.fromString(viewNameString);
		TimeBasedRetentionPolicy timeBasedRetentionPolicy = new TimeBasedRetentionPolicy(duration);
		retrievedMemberProperties =
				memberPropertiesRepository.findExpiredMemberProperties(viewName, timeBasedRetentionPolicy).toList();
	}

	@And("I retrieve the expired MemberProperties for {string} using VersionBasedRetentionPolicy with {int} versions")
	public void iRetrieveTheExpiredMemberPropertiesForVersionBasedRetentionPolicyWithVersions(String viewNameString,
																							  int versionsToKeep) {
		var viewName = ViewName.fromString(viewNameString);
		var versionBasedRetentionPolicy = new VersionBasedRetentionPolicy(versionsToKeep, null);
		retrievedMemberProperties =
				memberPropertiesRepository.findExpiredMemberProperties(viewName, versionBasedRetentionPolicy).toList();
	}
}
