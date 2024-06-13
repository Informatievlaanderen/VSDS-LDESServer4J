package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllocationRepositorySteps extends PostgresAllocationIntegrationTest {

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
				row.get("ids").isEmpty() ? List.of() : Arrays.stream(row.get("ids").split(",")).sorted().toList());
	}

	@DataTableType(replaceWithEmptyString = "[blank]")
	public RetrievedMemberMultipleIdsAllocations retrievedMemberMultipleIdsAllocations(Map<String, String> row) {
		return new RetrievedMemberMultipleIdsAllocations(
				Arrays.stream(row.get("fragmentIds").split(",")).collect(Collectors.toSet()),
				row.get("ids").isEmpty() ? List.of() : Arrays.stream(row.get("ids").split(",")).sorted().toList());
	}

	@Given("The following MemberAllocations")
	public void theFollowingMemberAllocations(List<MemberAllocation> memberAllocations) {
		this.memberAllocations = memberAllocations;
	}

	@And("They are ingested using the AllocationRepository")
	public void theyAreIngestedUsingTheAllocationRepository() {
		memberAllocations.forEach(allocationPostgresRepository::saveAllocation);
	}

	@Then("Querying by the fragment id has the following results")
	public void queryingByTheFragmentIdHasTheFollowingResults(
			List<RetrievedMemberAllocations> retrievedMemberAllocations) {
		retrievedMemberAllocations.forEach(retrievedMemberAllocation -> assertGetMemberAllocationsByFragmentId(
				retrievedMemberAllocation.fragmentId(), retrievedMemberAllocation.expectedIds()));
	}

	@Then("Querying by the fragment ids have the following results")
	public void queryingByTheFragmentIdsHaveTheFollowingResults(
			List<RetrievedMemberMultipleIdsAllocations> retrievedMemberAllocations) {
		retrievedMemberAllocations.forEach(retrievedMemberAllocation -> assertGetMemberAllocationIdsByFragmentIds(
				retrievedMemberAllocation.fragmentIds(), retrievedMemberAllocation.expectedIds()));
	}

	@When("^Deleting by the member id ([^ ]+) and the collection name ([^ ]+) and the view name ([^ ]+)")
	public void deletingByTheMemberIdMemberIdAndTheCollectionNameCollectionNameAndTheViewNameViewName(
			String memberId, String collectionName, String viewName) {
		allocationPostgresRepository.deleteByMemberIdAndCollectionNameAndViewName(memberId, collectionName, viewName);
	}

	@Then("^Querying by the fragment id ([^ ]+) has the following results ([^ ]+)")
	public void queryingByTheFragmentIdFragmentIdHasTheFollowingResultsIds(String fragmentId, String results) {
		List<String> expectedIds = results.equals("[blank]") ? List.of()
				: Arrays.stream(results.split(",")).sorted().toList();
		assertGetMemberAllocationsByFragmentId(fragmentId, expectedIds);
	}

	@When("^Deleting by the collection name ([^ ]+) and the view name ([^ ]+)")
	public void deletingByTheCollectionNameCollectionNameAndTheViewNameViewName(String collectionName,
			String viewName) {
		allocationPostgresRepository.deleteByCollectionNameAndViewName(collectionName, viewName);
	}

	@Then("^Querying by the fragment ids ([^ ]+) returns empty list")
	public void queryingByTheFragmentIdsFragmentIdsReturnsEmptyList(String fragmentIds) {
		Arrays.stream(fragmentIds.split(",")).forEach(fragmentId ->
				assertTrue(allocationPostgresRepository.getMemberAllocationsByFragmentId(fragmentId).toList().isEmpty()));
	}

	private void assertGetMemberAllocationsByFragmentId(String fragmentId, List<String> expectedIds) {
		List<MemberAllocation> memberAllocationsByFragmentId = allocationPostgresRepository
				.getMemberAllocationsByFragmentId(fragmentId).toList();
		List<String> actualIds = memberAllocationsByFragmentId.stream().map(MemberAllocation::id).sorted().toList();
		assertEquals(expectedIds, actualIds);
	}

	private void assertGetMemberAllocationIdsByFragmentIds(Set<String> fragmentIds, List<String> expectedIds) {
		List<String> memberAllocationsByFragmentIds = allocationPostgresRepository
				.getMemberAllocationIdsByFragmentIds(fragmentIds);
		List<String> actualIds = memberAllocationsByFragmentIds.stream().sorted().toList();
		assertEquals(expectedIds, actualIds);
	}

	@And("There are {int} remaining MemberAllocations in the MemberAllocationRepository")
	public void thereAreRemainingMemberAllocationsInTheMemberAllocationRepository(int expectedCount) {
		assertEquals(expectedCount, allocationEntityRepository.count());
	}

	@When("^Deleting by the collection name ([^ ]+)")
	public void deletingByTheCollectionNameCollectionName(String collectionName) {
		allocationPostgresRepository.deleteByCollectionName(collectionName);
	}

	@When("^Deleting by the fragment ids ([^ ]+)")
	public void deletingByTheFragmentIdFragmentId(String fragmentIds) {
		allocationPostgresRepository.deleteAllByFragmentId(Set.of(fragmentIds.trim().split(",")));
	}

	@Then("^The compaction candidates for ([^ ]+) with capacity of ([^ ]+) contains ([^ ]+)")
	public void theCompactionCandidatesForViewNameWithCapacityOfCapacityContainsExpectedFragments(
			String viewName, int capacity, String expectedFragments) {
		var compactionCandidates = allocationPostgresRepository
				.getPossibleCompactionCandidates(ViewName.fromString(viewName), capacity)
				.map(CompactionCandidate::getId)
				.sorted()
				.toList();

		List<String> expectedFragmentList = Arrays.stream(expectedFragments.split(",")).toList();

		assertEquals(expectedFragmentList, compactionCandidates);
	}

	public record RetrievedMemberAllocations(String fragmentId, List<String> expectedIds) {
	}
	public record RetrievedMemberMultipleIdsAllocations(Set<String> fragmentIds, List<String> expectedIds) {
	}
}
