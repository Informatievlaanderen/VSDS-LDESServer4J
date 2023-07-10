package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class MemberRepositorySteps extends MongoIngestIntegrationTest {

	private List<Member> members;
	private Optional<Member> retrievedMember;

	@When("I save the members using the MemberRepository")
	public void iSaveTheMembers() {
		members.forEach(memberRepository::saveMember);
	}

	@DataTableType(replaceWithEmptyString = "[blank]")
	public Member memberEntryTransformer(Map<String, String> row) {
		return new Member(
				row.get("id"),
				row.get("collectionName"),
				row.get("sequenceNr").equals("") ? null : Long.parseLong(row.get("sequenceNr")),
				ModelFactory.createDefaultModel());
	}

	@Given("The following members")
	public void theFollowingMembers(List<Member> members) {
		this.members = members;
	}

	@Then("The member with id {string} can be retrieved from the database")
	public void theMemberWithIdCanBeRetrievedFromTheDatabase(String id) {
		retrievedMember = memberRepository.findById(id);
	}

	@And("The retrieved member has the same properties as the {int} member in the table and has sequenceNr {int}")
	public void theRetrievedMemberHasTheSamePropertiesAsTheMemberInTheTableAndHasSequenceNr(int index, int sequencNr) {
		assertTrue(retrievedMember.isPresent());
		Member retrievedMemberPresent = retrievedMember.get();
		Member member = members.get(index - 1);
		assertEquals(member.getId(), retrievedMemberPresent.getId());
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
		List<Member> result = memberRepository.getMemberStreamOfCollection(collectionName).toList();
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
		members = memberRepository.findAllByIds(table.column(0).stream().toList());
	}

	@Then("I expect a list of {int} members")
	public void iExpectAListOfMembers(int memberCount) {
		assertEquals(memberCount, members.size());
	}
}
