package be.vlaanderen.informatievlaanderen.ldes.server.fetch;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllocationRepositorySteps extends MongoAllocationIntegrationTest {

	private List<MemberAllocation> memberAllocations;
	private List<MemberAllocation> retrievedMemberAllocations;

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
				row.get("ids").equals("") ? List.of() : Arrays.stream(row.get("ids").split(",")).toList());
	}

	@Given("The following MemberAllocations")
	public void theFollowingMemberAllocations(List<MemberAllocation> memberAllocations) {
		this.memberAllocations = memberAllocations;
	}

	@And("They are ingested using the AllocationRepository")
	public void theyAreIngestedUsingTheAllocationRepository() {
		memberAllocations.forEach(allocationMongoRepository::saveAllocation);
	}

	@Then("Querying using the fragment ids has the following results")
	public void queryingUsingTheFragmentIdsHasTheFollowingResults(
			List<RetrievedMemberAllocations> retrievedMemberAllocations) {
		retrievedMemberAllocations.forEach(retrievedMemberAllocation -> {
			List<MemberAllocation> memberAllocationsByFragmentId = allocationMongoRepository
					.getMemberAllocationsByFragmentId(retrievedMemberAllocation.fragmentId());
			List<String> actualIds = memberAllocationsByFragmentId.stream().map(MemberAllocation::getId).toList();
			assertEquals(retrievedMemberAllocation.id, actualIds);
		});
	}

	private record RetrievedMemberAllocations(String fragmentId, List<String> id) {
	}
}
