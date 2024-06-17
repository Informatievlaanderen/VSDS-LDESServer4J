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
		memberRepository.insertAll(members);
		this.members.addAll(members);
	}

	@DataTableType(replaceWithEmptyString = "[blank]")
	public IngestedMember memberEntryTransformer(Map<String, String> row) {
		return new IngestedMember(
				row.get("id"),
				row.get("collectionName"),
				row.get("versionOf"),
				LocalDateTime.parse(row.get("timestamp")),
				null,
				true,
				UUID.randomUUID().toString(),
				ModelFactory.createDefaultModel());
	}

	@Then("The member with id {string} can be retrieved from the database")
	public void theMemberWithIdCanBeRetrievedFromTheDatabase(String id) {
		retrievedMember = memberRepository.findAllByIds(List.of(id)).findFirst();
		assertTrue(retrievedMember.isPresent());
	}

	@And("The retrieved member has the same properties as the {int} member in the table and has sequenceNr {int}")
	public void theRetrievedMemberHasTheSamePropertiesAsTheMemberInTheTableAndHasSequenceNr(int index, int sequencNr) {
		assertTrue(retrievedMember.isPresent());
		IngestedMember retrievedMemberPresent = retrievedMember.get();
		IngestedMember member = members.get(index - 1);
		assertEquals(member.getSubject(), retrievedMemberPresent.getSubject());
		assertEquals(member.getCollectionName(), retrievedMemberPresent.getCollectionName());
		assertEquals(sequencNr, retrievedMemberPresent.getSequenceNr());
	}

	@Then("The member with id {string} will exist")
	public void theMemberWithIdWillExist(String id) {
		assertTrue(memberRepository.memberExists(id));
	}

	@And("The member with id {string} will not exist")
	public void thenTheMemberWithIdWillNotExist(String id) {
		assertFalse(memberRepository.memberExists(id));
	}

	@When("I delete all the members of collection {string}")
	public void iDeleteAllTheMembersOfCollection(String collectionName) {
		memberRepository.deleteMembersByCollection(collectionName);
	}

	@Then("I can get an ordered stream from all the members of the {string} collection containing {int} members")
	public void iCanGetAnOrderedStreamFromAllTheMembersOfTheCollection(String collectionName, int memberCount) {
		List<IngestedMember> result = memberRepository.getMemberStreamOfCollection(collectionName).toList();
		for (int i = 0; i < result.size(); i++) {
			assertEquals(i, result.get(i).getSequenceNr().intValue());
		}

		assertEquals(memberCount, result.size());
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

	@Then("The number of members is {int}")
	public void theRetrievedMemberIsEmpty(int memberCount) {
		assertEquals(memberCount, memberRepository.getMemberStreamOfCollection("mobility-hindrances").count() +
				memberRepository.getMemberStreamOfCollection("other").count());
	}

	@Then("The number of members of the {string} collection is {int}")
	public void theRetrievedMemberIsEmpty(String collectionName, int memberCount) {
		assertEquals(memberCount, memberRepository.getMemberStreamOfCollection(collectionName).count());
	}

	@Then("I delete all members from the {string} collection")
	public void theRetrievedMemberIsEmpty(String collectionName) {
		memberRepository.deleteMembersByCollection(collectionName);
	}
}
