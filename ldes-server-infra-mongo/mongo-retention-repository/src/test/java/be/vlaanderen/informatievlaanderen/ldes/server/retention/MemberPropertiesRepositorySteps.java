package be.vlaanderen.informatievlaanderen.ldes.server.retention;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MemberPropertiesRepositorySteps extends MongoRetentionIntegrationTest {

	private List<MemberProperties> memberProperties;
	private List<MemberProperties> retrievedMemberProperties;

	@DataTableType
	public MemberProperties memberPropertiesEntryTransformer(Map<String, String> row) {
		String string = LocalDateTime.now().toString();
		System.out.println(string);
		return new MemberProperties(
				row.get("id"),
				row.get("collectionName"),
				row.get("versionOf"),
				LocalDateTime.parse(row.get("timestamp")));
	}

	@Given("The following MemberProperties")
	public void theFollowingMemberProperties(List<MemberProperties> memberProperties) {
		this.memberProperties = memberProperties;
	}

	@When("I save the MemberProperties using the MemberPropertiesRepository")
	public void iSaveTheMemberPropertiesUsingTheMemberPropertiesRepository() {
		memberProperties.forEach(memberPropertiesRepository::save);
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

	@And("I retrieve all MemberProperties with versionOf {string}")
	public void iRetrieveAllMemberPropertiesWithVersionOf(String versionOf) {
		retrievedMemberProperties = memberPropertiesRepository.getMemberPropertiesOfVersion(versionOf);
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

	@And("I retrieve all MemberProperties with view {string}")
	public void iRetrieveAllMemberPropertiesWithView(String viewName) {
		retrievedMemberProperties = memberPropertiesRepository.getMemberPropertiesWithViewReference(viewName).toList();
	}

	@And("I remove the view with name {string} of the MemberProperties with id {string}")
	public void iRemoveTheViewWithNameOfTheMemberPropertiesWithId(String viewName, String id) {
		memberPropertiesRepository.removeViewReference(id, viewName);
	}

	@And("The retrieved MemberProperties does not have the view {string} as a property")
	public void theRetrievedMemberPropertiesDoesNotHaveTheViewAsAProperty(String viewName) {
		assertFalse(retrievedMemberProperties.get(0).containsViewReference(viewName));
	}

	@And("I delete the MemberProperties with id {string}")
	public void iDeleteTheMemberPropertiesWithId(String id) {
		memberPropertiesRepository.deleteById(id);
	}
}
