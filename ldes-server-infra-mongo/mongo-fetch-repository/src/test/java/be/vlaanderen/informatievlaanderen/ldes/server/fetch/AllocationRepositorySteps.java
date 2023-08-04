package be.vlaanderen.informatievlaanderen.ldes.server.fetch;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllocationRepositorySteps extends MongoAllocationIntegrationTest {

	private List<MemberAllocation> memberAllocations;

	@DataTableType
	public MemberAllocation memberAllocationEntryTransformer(Map<String, String> row) {
		return new MemberAllocation(
				row.get("id"),
				row.get("collectionName"),
				row.get("viewName"),
				row.get("fragmentId"),
				row.get("memberId"));
	}

	@DataTableType(replaceWithEmptyString = "[blank]")
	public RetrievedMemberAllocations retrievedMemberAllocationsEntryTransformer(Map<String, String> row) {
		return new RetrievedMemberAllocations(
				row.get("fragmentId"),
				row.get("ids").equals("") ? List.of() : Arrays.stream(row.get("ids").split(",")).sorted().toList());
	}

	@Given("The following MemberAllocations")
	public void theFollowingMemberAllocations(List<MemberAllocation> memberAllocations) {
		this.memberAllocations = memberAllocations;
	}

	@And("They are ingested using the AllocationRepository")
	public void theyAreIngestedUsingTheAllocationRepository() {
		memberAllocations.forEach(allocationMongoRepository::saveAllocation);
	}

	@Then("Querying by the fragment id has the following results")
	public void queryingByTheFragmentIdHasTheFollowingResults(
			List<RetrievedMemberAllocations> retrievedMemberAllocations) {
		retrievedMemberAllocations.forEach(retrievedMemberAllocation -> assertMemberAllocationIdsEqualExpectedIds(
				retrievedMemberAllocation.fragmentId(), retrievedMemberAllocation.expectedIds()));
	}

	@When("^Deleting by the member id ([^\s]+) and the

	collection name ([^\s]+) and the

	view name ([^\s]+)")

	public void deletingByTheMemberIdMemberIdAndTheCollectionNameCollectionNameAndTheViewNameViewName(String memberId,
			String collectionName, String viewName) {
		allocationMongoRepository.deleteByMemberIdAndCollectionNameAndViewName(memberId, collectionName, viewName);
	}

	@Then
	("^Querying by the fragment id ([^\s]+) has the following results ([^\s]+)")public void queryingByTheFragmentIdFragmentIdHasTheFollowingResultsIds(
			String fragmentId, String results) {
		List<String> expectedIds = results.equals("[blank]") ? List.of()
				: Arrays.stream(results.split(",")).sorted().toList();
		assertMemberAllocationIdsEqualExpectedIds(fragmentId, expectedIds);
	}

	@When
	("^Deleting by the collection name ([^\s]+) and the view name ([^\s]+)")public void deletingByTheCollectionNameCollectionNameAndTheViewNameViewName(
			String collectionName, String viewName) {
		allocationMongoRepository.deleteByCollectionNameAndViewName(collectionName, viewName);
	}

	@Then
	("^Querying by the fragment ids ([^\s]+) returns empty list")public void queryingByTheFragmentIdsFragmentIdsReturnsEmptyList(
			String fragmentIds) {
		Arrays.stream(fragmentIds.split(",")).forEach(fragmentId -> {
			assertTrue(allocationMongoRepository.getMemberAllocationsByFragmentId(fragmentId).isEmpty());
		});
	}

	private void assertMemberAllocationIdsEqualExpectedIds(String fragmentId, List<String> expectedIds) {
		List<MemberAllocation> memberAllocationsByFragmentId = allocationMongoRepository
				.getMemberAllocationsByFragmentId(fragmentId);
		List<String> actualIds = memberAllocationsByFragmentId.stream().map(MemberAllocation::getId).sorted().toList();
		assertEquals(expectedIds, actualIds);
	}

	@And("There are {int} remaining MemberAllocations in the MemberAllocationRepository")
	public void thereAreRemainingMemberAllocationsInTheMemberAllocationRepository(int expectedCount) {
		assertEquals(expectedCount, allocationEntityRepository.count());
	}

	@When
	("^Deleting by the collection name ([^\s]+)")public void deletingByTheCollectionNameCollectionName(
			String collectionName) {
		allocationMongoRepository.deleteByCollectionName(collectionName);
	}

	private record RetrievedMemberAllocations(String fragmentId, List<String> expectedIds) {
	}
}
