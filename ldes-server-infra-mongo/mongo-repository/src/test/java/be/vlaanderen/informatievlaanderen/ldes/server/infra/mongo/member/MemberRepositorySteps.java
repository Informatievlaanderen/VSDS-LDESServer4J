package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.SpringIntegrationTest;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.ModelFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class MemberRepositorySteps extends SpringIntegrationTest {

	private List<Member> members;
	private Optional<Member> retrievedMember;

	@When("I save the members using the MemberRepository")
	public void iSaveTheMembers() {
		members.forEach(memberRepository::saveLdesMember);
	}

	@DataTableType(replaceWithEmptyString = "[blank]")
	public Member memberEntryTransformer(Map<String, String> row) {
		return new Member(
				row.get("id"),
				row.get("collectionName"),
				row.get("sequenceNr").equals("") ? null : Long.parseLong(row.get("sequenceNr")),
				row.get("versionOf"), LocalDateTime.now(),
				ModelFactory.createDefaultModel(), new ArrayList<>());
	}

	@Given("The following members")
	public void theFollowingMembers(List<Member> members) {
		this.members = members;
	}

	@Then("The member with id {string} can be retrieved from the database")
	public void theMemberWithIdCanBeRetrievedFromTheDatabase(String id) {
		retrievedMember = memberRepository.getMember(id);
	}

	@And("The retrieved member has the same properties as the {int} member in the table and has sequenceNr {int}")
	public void theRetrievedMemberHasTheSamePropertiesAsTheMemberInTheTableAndHasSequenceNr(int index, int sequencNr) {
		assertTrue(retrievedMember.isPresent());
		Member retrievedMemberPresent = retrievedMember.get();
		Member member = members.get(index - 1);
		assertEquals(member.getLdesMemberId(), retrievedMemberPresent.getLdesMemberId());
		assertEquals(member.getCollectionName(), retrievedMemberPresent.getCollectionName());
		assertEquals(sequencNr, retrievedMemberPresent.getSequenceNr());
		assertEquals(member.getVersionOf(), retrievedMemberPresent.getVersionOf());
		assertThat(member.getTimestamp()).isEqualToIgnoringNanos(retrievedMemberPresent.getTimestamp());
		assertEquals(member.getTreeNodeReferences(), retrievedMemberPresent.getTreeNodeReferences());
	}

	@Then("The member with id {string} will exist")
	public void theMemberWithIdWillExist(String id) {
		assertTrue(memberRepository.memberExists(id));
	}

	@And("The member with id {string} will not exist")
	public void theMemberWithIdWillNotExist(String id) {
		assertFalse(memberRepository.memberExists(id));
	}

	@When("I delete all the members of collection {string}")
	public void iDeleteAllTheMembersOfCollection(String collectionName) {
		memberRepository.deleteMembersByCollection(collectionName);
	}

	@And("The following treeNodeReferences on the members")
	public void theFollowingTreeNodeReferencesOnTheMembers(List<String> treeNodeReferences) {
		members.forEach(member -> member.getTreeNodeReferences()
				.addAll(treeNodeReferences.stream().map(LdesFragmentIdentifier::fromFragmentId).toList()));
	}

	@When("I remove the view references of view {string}")
	public void iRemoveTheViewReferencesOfViewByPage(String viewName) {
		memberRepository.removeViewReferences(ViewName.fromString(viewName));
	}

	@And("The members of collection {string} will only have treeNodeReference {string}")
	public void theMembersWillOnlyHaveTreeNodeReference(String collectionName, String treeNodeReference) {
		List<Member> membersInDb = memberRepository.getMemberStreamOfCollection(collectionName).toList();
		assertEquals(3, membersInDb.size());
		membersInDb.forEach(member -> {
			assertEquals(1, member.getTreeNodeReferences().size());
			assertEquals(treeNodeReference, member.getTreeNodeReferences().get(0).asString());
		});
	}
}
