package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.ModelFactory;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class MemberRepositorySteps extends PostgresIngestIntegrationTest {

	private List<IngestedMember> members = new ArrayList<>();
	private Optional<IngestedMember> retrievedMember;

	@When("I save the members using the MemberRepository")
	public void iSaveTheMembers(List<IngestedMember> members) {
		List<IngestedMember> actualIngestedMembers = memberRepository.insertAll(members);
		this.members.addAll(actualIngestedMembers);
	}

	@DataTableType(replaceWithEmptyString = "[blank]")
	public IngestedMember memberEntryTransformer(Map<String, String> row) {
		return new IngestedMember(
				row.get("subject"),
				row.get("collectionName"),
				row.get("versionOf"),
				LocalDateTime.parse(row.get("timestamp")),
				true,
				UUID.randomUUID().toString(),
				ModelFactory.createDefaultModel());
	}

	@Then("The member with id {string} can be retrieved from the database")
	public void theMemberWithIdCanBeRetrievedFromTheDatabase(String id) {
		retrievedMember = memberRepository.findAllByIds(List.of(id)).findFirst();
		assertTrue(retrievedMember.isPresent());
	}

	@And("The retrieved member has the same properties as the {int} member in the table")
	public void theRetrievedMemberHasTheSamePropertiesAsTheMemberInTheTable(int index) {
		assertTrue(retrievedMember.isPresent());
		IngestedMember retrievedMemberPresent = retrievedMember.get();
		IngestedMember member = members.get(index - 1);
		assertEquals(member.getSubject(), retrievedMemberPresent.getSubject());
		assertEquals(member.getCollectionName(), retrievedMemberPresent.getCollectionName());
	}

	@Then("The member with id {string} will exist")
	public void theMemberWithIdWillExist(String id) {
		assertEquals(1, memberRepository.findAllByIds(List.of(id)).count());
	}

	@And("The member with id {string} will not exist")
	public void thenTheMemberWithIdWillNotExist(String id) {
		assertEquals(0, memberRepository.findAllByIds(List.of(id)).count());
	}

	@DataTableType(replaceWithEmptyString = "[blank]")
	public List<String> memberIdTransformer(Map<String, String> row) {
		return row.values().stream().toList();
	}

	@When("I try to retrieve the following members by Id")
	public void iTryToRetrieveTheFollowingMembersByIdMemberIds(@Transpose DataTable table) {
		members = memberRepository.findAllByIds(table.column(0).stream().toList()).toList();
	}

	@Then("I expect a list of {int} members")
	public void iExpectAListOfMembers(int memberCount) {
		assertEquals(memberCount, members.size());
	}

	@When("I delete the member with id {string}")
	public void iDeleteMemberWithId(String memberId) {
		memberRepository.deleteMembers(List.of(memberId));
	}

	@Then("The retrieved member is empty")
	public void theRetrievedMemberIsEmpty() {
		assertTrue(retrievedMember.isEmpty());
	}
}
