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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class MemberPropertiesRepositorySteps extends MongoRetentionIntegrationTest {

	private List<MemberProperties> memberProperties;
	private Optional<MemberProperties> retrievedMemberProperties;

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

	@Then("The MemberProperties with id {string} can be retrieved from the database")
	public void theMemberPropertiesWithIdCanBeRetrievedFromTheDatabase(String id) {
		retrievedMemberProperties = memberPropertiesRepository.retrieve(id);
	}

	@And("The retrieved MemberProperties has the same properties as the {int} MemberProperties in the table")
	public void theRetrievedMemberPropertiesHasTheSamePropertiesAsTheMemberPropertiesInTheTable(int index) {
		assertTrue(retrievedMemberProperties.isPresent());
		MemberProperties retrievedMemberPropertiesPresent = retrievedMemberProperties.get();
		MemberProperties expectedMemberProperties = memberProperties.get(index - 1);
		assertEquals(expectedMemberProperties.getId(), retrievedMemberPropertiesPresent.getId());
		assertEquals(expectedMemberProperties.getCollectionName(),
				retrievedMemberPropertiesPresent.getCollectionName());
		assertEquals(expectedMemberProperties.getVersionOf(), retrievedMemberPropertiesPresent.getVersionOf());
		assertEquals(expectedMemberProperties.getTimestamp(), retrievedMemberPropertiesPresent.getTimestamp());
	}

	@And("The retrieved MemberProperties has the view {string} as a property")
	public void theMemberPropertyContainsTheView(String view) {
		assertTrue(retrievedMemberProperties.get().containsViewReference(view));
	}
}
