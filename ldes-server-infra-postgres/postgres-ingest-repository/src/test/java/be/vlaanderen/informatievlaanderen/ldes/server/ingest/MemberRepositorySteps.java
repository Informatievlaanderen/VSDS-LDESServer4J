package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.ModelFactory;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.InstanceOfAssertFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class MemberRepositorySteps extends PostgresIngestIntegrationTest {

	private List<IngestedMember> members = new ArrayList<>();
	private Optional<IngestedMember> retrievedMember;

	@When("I save the members using the MemberRepository")
	public void iSaveTheMembers(List<IngestedMember> members) {
		members.stream()
				.collect(Collectors.groupingBy(IngestedMember::getCollectionName))
				.values().stream()
				.filter(groupedMembers -> memberRepository.insertAll(groupedMembers) != 0)
				.forEach(this.members::addAll);
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

	@Then("The member with collection {string} and subject {string} can be retrieved from the database")
	public void theMemberWithIdCanBeRetrievedFromTheDatabase(String collection, String id) {
		retrievedMember = memberRepository.findAllByCollectionAndSubject(collection, List.of(id)).findFirst();
		assertThat(retrievedMember).isPresent();
	}

	@And("The retrieved member has the same properties as the {int} member in the table")
	public void theRetrievedMemberHasTheSamePropertiesAsTheMemberInTheTable(int index) {
		IngestedMember member = members.get(index - 1);
		assertThat(retrievedMember)
				.isPresent()
				.get(new InstanceOfAssertFactory<>(IngestedMember.class, (AssertFactory<IngestedMember, AbstractAssert<?, ?>>) IngestedMemberAssert::new))
				.isEqualTo(member);
	}

	@Then("The member with collection {string} and subject {string} will exist")
	public void theMemberWithCollectionAndSubjectWillExist(String collection, String subject) {
		assertThat(memberRepository.findAllByCollectionAndSubject(collection, List.of(subject))).isNotEmpty();
	}

	@And("The member with collection {string} and subject {string} will not exist")
	public void thenTheMemberWithCollectionAndSubjectWillNotExist(String collection, String subject) {
		assertThat(memberRepository.findAllByCollectionAndSubject(collection, List.of(subject))).isEmpty();
	}

	@DataTableType(replaceWithEmptyString = "[blank]")
	public List<String> memberIdTransformer(Map<String, String> row) {
		return row.values().stream().toList();
	}

	@Then("I expect a list of {int} members")
	public void iExpectAListOfMembers(int memberCount) {
		assertEquals(memberCount, members.size());
	}

	@When("I delete the member with collection {string} and subject {string}")
	public void iDeleteMemberWithCollectionAndSubject(String collection, String subject) {
		memberRepository.deleteMembersByCollectionNameAndSubjects(collection, List.of(subject));
	}

	@When("I delete collection {string}")
	public void iDeleteCollection(String collection) {
		eventStreamRepository.deleteEventStream(collection);
	}

	private static class IngestedMemberAssert extends AbstractAssert<IngestedMemberAssert, IngestedMember> {
		protected IngestedMemberAssert(IngestedMember ingestedMember) {
			super(ingestedMember, IngestedMemberAssert.class);
		}

		@Override
		public IngestedMemberAssert isEqualTo(Object expected) {
			isNotNull();

			if (!(expected instanceof IngestedMember expectedMember)) {
				failWithMessage("Incompatible comparison types: expected = %s, actual = %s", expected.getClass(), actual.getClass());
			} else if (!expectedMember.getSubject().equals(actual.getSubject()) || !expectedMember.getCollectionName().equals(actual.getCollectionName())) {
				failWithMessage("Expected fields does not match");
			}

			return this;
		}
	}
}
