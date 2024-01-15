package be.vlaanderen.informatievlaanderen.ldes.server.compaction;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.BulkMemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.mockito.ArgumentMatchers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@SuppressWarnings("java:S3415")
public class CompactionServiceSteps extends CompactionIntegrationTest {

	@DataTableType
	public ViewSpecification ViewSpecificationEntryTransformer(Map<String, String> row) {
		return new ViewSpecification(
				ViewName.fromString(row.get("viewName")),
				List.of(), List.of(), Integer.parseInt(row.get("pageSize")));
	}

	@DataTableType
	public FragmentAllocations FragmentAllocationsListEntryTransformer(Map<String, String> row) {
		List<MemberAllocation> memberAllocations = new ArrayList<>();
		String fragmentIdentifier = row.get("fragmentIdentifier");
		for (String memberId : row.get("members").split(",")) {
			memberAllocations.add(new MemberAllocation(fragmentIdentifier + "/" + memberId, "mobility-hindrances",
					"by-page", fragmentIdentifier, memberId));
		}
		return new FragmentAllocations(fragmentIdentifier, memberAllocations);
	}

	@DataTableType
	public MemberFragmentations MemberFragmentationsEntryTransformer(Map<String, String> row) {
		return new MemberFragmentations(row.get("fragmentId"), Arrays.stream(row.get("memberIds").split(",")).toList());
	}

	@DataTableType(replaceWithEmptyString = "[blank]")
	public Fragment FragmentEntryTransformer(Map<String, String> row) {
		return new Fragment(
				LdesFragmentIdentifier.fromFragmentId(row.get("fragmentIdentifier")),
				Boolean.parseBoolean(row.get("immutable")), Integer.parseInt(row.get("nrOfMembersAdded")),
				row.get("relation").isEmpty() ? new ArrayList<>()
						: Arrays.stream(row.get("relation").split(",")).map(treeNode -> new TreeRelation("",
								LdesFragmentIdentifier.fromFragmentId(treeNode), "", "", GENERIC_TREE_RELATION))
						.collect(Collectors.toList()),
				null);
	}

	@Given("a view with the following properties")
	public void aViewWithTheFollowingProperties(ViewSpecification viewSpecification) {
		applicationEventPublisher.publishEvent(new ViewAddedEvent(viewSpecification));
	}

	@And("the following Fragments are available")
	public void theFollowingFragmentsAreAvailable(List<Fragment> fragments) {
		fragments.forEach(fragment -> {
			when(fragmentRepository.retrieveFragment(fragment.getFragmentId())).thenReturn(Optional.of(fragment));
			when(fragmentRepository.retrieveFragment(fragment.getFragmentId())).thenReturn(Optional.of(fragment));
			if (fragment.getFragmentPairs().isEmpty()) {
				when(fragmentRepository.retrieveRootFragment(fragment.getViewName().asString()))
						.thenReturn(Optional.of(fragment));
			}
			fragment.getRelations()
					.forEach(treeRelation -> when(
							fragmentRepository.retrieveFragmentsByOutgoingRelation(treeRelation.treeNode()))
							.thenReturn(List.of(fragment)));
		});
	}

	@And("the following allocations are present")
	public void theFollowingAllocationsArePresent(List<FragmentAllocations> fragmentAllocations) {
		when(allocationRepository.getPossibleCompactionCandidates(any(ViewName.class), anyInt()))
				.thenAnswer(i ->
						getAllocationAggregates(fragmentAllocations,
								i.getArgument(0, ViewName.class),
								i.getArgument(1, Integer.class))
				);

		when(allocationRepository.getMemberAllocationIdsByFragmentIds(ArgumentMatchers.any()))
				.thenAnswer(x -> {
					Set<String> requested = x.getArgument(0);
					return fragmentAllocations.stream()
							.filter(fragmentAllocation -> requested.contains(fragmentAllocation.fragmentId))
							.flatMap(fragmentAllocations1 -> fragmentAllocations1.memberAllocations.stream()
									.map(MemberAllocation::getMemberId)).toList();
				});
	}

	private Stream<CompactionCandidate> getAllocationAggregates(List<FragmentAllocations> fragmentAllocations, ViewName viewName, Integer viewCapacity) {
		return fragmentAllocations.stream()
				.filter(fragmentAllocation -> {
					var fragmentId = LdesFragmentIdentifier.fromFragmentId(fragmentAllocation.fragmentId);
					return fragmentId.getViewName().equals(viewName);
				})
				.map(fragmentAllocation -> new CompactionCandidate(fragmentAllocation.fragmentId, fragmentAllocation.memberAllocations.size()));
	}

	@And("verify creation of the following fragments")
	public void verifyCreationOfTheFollowingFragments(List<String> createdFragments) {
		createdFragments.forEach(createdFragment -> verify(fragmentRepository)
				.saveFragment(new Fragment(LdesFragmentIdentifier.fromFragmentId(createdFragment))));
	}

	@And("verify update of predecessor relations")
	public void verifyUpdateOfPredecessorRelations(List<String> predecessorFragments) {
		predecessorFragments.forEach(predecessorFragment -> {
			verify(fragmentRepository)
					.saveFragment(new Fragment(LdesFragmentIdentifier.fromFragmentId(predecessorFragment)));

			List<TreeRelation> treeRelations = fragmentRepository
					.retrieveFragment(LdesFragmentIdentifier.fromFragmentId(predecessorFragment))
					.orElseThrow()
					.getRelations();
			assertThat(treeRelations)
					.map(TreeRelation::treeNode)
					.map(LdesFragmentIdentifier::asString)
					.filteredOn(identifier -> !identifier.contains("dummy"))
					.hasSize(1);
		});
	}

	@And("verify fragmentation of members")
	public void verifyFragmentationOfMembers(List<MemberFragmentations> memberFragmentations) {
		verify(eventConsumer, times(memberFragmentations.size())).consumeEvent(any(BulkMemberAllocatedEvent.class));
		memberFragmentations.forEach(memberFragmentation -> {
			verify(fragmentRepository).incrementNrOfMembersAdded(LdesFragmentIdentifier.fromFragmentId(memberFragmentation.fragmentId), memberFragmentation.memberIds.size());
		});
	}

	@Then("wait for {int} seconds until compaction has executed at least once")
	public void waitForSecondsUntilCompactionHasExecutedAtLeastOnce(int secondsToWait) {
		await()
				.timeout(secondsToWait + 1, SECONDS)
				.pollDelay(secondsToWait, SECONDS)
				.untilAsserted(() -> assertThat(true).isTrue());
	}

	public record FragmentAllocations(String fragmentId, List<MemberAllocation> memberAllocations) {
	}

	public record MemberFragmentations(String fragmentId, List<String> memberIds) {
	}

}
