package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.MongoFragmentationIntegrationTest;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.*;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FragmentRepositorySteps extends MongoFragmentationIntegrationTest {

	private List<Fragment> fragments;
	private Optional<Fragment> retrievedLdesFragment;

	@DataTableType(replaceWithEmptyString = "[blank]")
	public Fragment ldesFragmentEntryTransformer(Map<String, String> row) {
		return new Fragment(new LdesFragmentIdentifier(
				ViewName.fromString(row.get("viewName")),
                row.get("fragmentPairs").isEmpty() ? List.of() : getFragmentPairs(row.get("fragmentPairs"))),
				Boolean.parseBoolean(row.get("immutable")),
				Integer.parseInt(row.get("nrOfMembersAdded")),
				List.of(), null);
	}

	@DataTableType(replaceWithEmptyString = "[blank]")
	public FragmentWithRelation fragmentWithRelationsEntryTransformer(Map<String, String> row) {
		return new FragmentWithRelation(new Fragment(new LdesFragmentIdentifier(
				ViewName.fromString(row.get("viewName")),
                row.get("fragmentPairs").isEmpty() ? List.of() : getFragmentPairs(row.get("fragmentPairs"))),
				false,
				0,
                row.get("relations").isEmpty() ? List.of()
						: Arrays.stream(row.get("relations").split(",")).map(treeNode -> new TreeRelation("",
								LdesFragmentIdentifier.fromFragmentId(treeNode), "", "", GENERIC_TREE_RELATION))
								.toList(),
				null));
	}

	@DataTableType(replaceWithEmptyString = "[blank]")
	public OutgoingRelationResult outgoingRelationResultEntryTransformer(Map<String, String> row) {
		return new OutgoingRelationResult(LdesFragmentIdentifier.fromFragmentId(row.get("outgoingRelation")),
                row.get("fragmentIds").isEmpty() ? Set.of()
						: Arrays.stream(row.get("fragmentIds").split(",")).map(LdesFragmentIdentifier::fromFragmentId)
								.collect(Collectors.toSet()));
	}

	private List<FragmentPair> getFragmentPairs(String row) {
		String[] fragmentPairs = row.split(",");
		List<FragmentPair> fragmentPairList = new ArrayList<>();
		for (int i = 0; i < fragmentPairs.length; i += 2) {
			fragmentPairList.add(new FragmentPair(fragmentPairs[i], fragmentPairs[i + 1]));
		}
		return fragmentPairList;
	}

	@Before
	public void setup() {
		retrievedLdesFragment = Optional.empty();
		fragments = List.of();
		fragmentRepository.deleteTreeNodesByCollection("mobility-hindrances");
		fragmentRepository.deleteTreeNodesByCollection("parcels");
	}

	@Given("The following ldesFragments")
	public void theFollowingLdesFragment(List<Fragment> fragments) {
		this.fragments = fragments;
	}

	@When("I save the ldesFragments using the LdesFragmentRepository")
	public void iSaveTheLdesFragmentUsingTheLdesFragmentRepository() {
		fragments.forEach(ldesFragment -> fragmentRepository.saveFragment(ldesFragment));
	}

	@Then("The ldesFragment with id {string} can be retrieved from the database")
	public void theLdesFragmentWithIdCanBeRetrievedFromTheDatabase(String fragmentId) {
		retrievedLdesFragment = fragmentRepository.retrieveFragment(LdesFragmentIdentifier.fromFragmentId(fragmentId));
	}

	@Then("the repository contains {int} ldesFragments with viewname {string}")
	public void theRepositoryContainsLdesFragmentsWithViewname(int expectedNumberOfFragments, String viewName) {
		assertEquals(expectedNumberOfFragments, fragmentRepository.retrieveFragmentsOfView(viewName).count());
	}

	@And("The retrieved ldesFragment has the same properties as ldesFragment {int}")
	public void theRetrievedLdesFragmentHasTheSamePropertiesAsLdesFragment(int index) {
		assertTrue(retrievedLdesFragment.isPresent());
		Fragment actualFragment = this.retrievedLdesFragment.get();
		Fragment expectedFragment = this.fragments.get(index - 1);
		assertEquals(expectedFragment.getFragmentId(), actualFragment.getFragmentId());
		assertEquals(expectedFragment.getViewName(), actualFragment.getViewName());
		assertEquals(expectedFragment.getFragmentPairs(), actualFragment.getFragmentPairs());
		assertEquals(expectedFragment.isImmutable(), actualFragment.isImmutable());
		assertEquals(expectedFragment.getNrOfMembersAdded(), actualFragment.getNrOfMembersAdded());
		assertEquals(expectedFragment.getRelations(), actualFragment.getRelations());
	}

	@When("I delete the ldesFragments of viewName {string}")
	public void iDeleteTheLdesFragmentsOfViewName(String viewName) {
		fragmentRepository.removeLdesFragmentsOfView(viewName);
	}

	@When("I delete the collection {string}")
	public void iDeleteTheCollection(String collectionName) {
		fragmentRepository.deleteTreeNodesByCollection(collectionName);
	}

	@When("I retrieve the open child fragment of fragment {int}")
	public void retrieveOpenFragment(int index) {
		retrievedLdesFragment = fragmentRepository.retrieveOpenChildFragment(fragments.get(index - 1).getFragmentId());
	}

	@When("I retrieve the root fragment of the view with viewname {string}")
	public void retrieveRootFragment(String viewName) {
		retrievedLdesFragment = fragmentRepository.retrieveRootFragment(viewName);
	}

	@When("I increment the number of members of fragment {int}")
	public void incrementMembers(int index) {
		fragmentRepository.incrementNrOfMembersAdded(fragments.get(index - 1).getFragmentId());
	}

	@Then("The retrieved ldesFragment has {int} members")
	public void theRetrievedLdesFragmentHasGivenNrOfMembersAdded(int nrOfMembersAdded) {
		assertTrue(retrievedLdesFragment.isPresent());
		Fragment actualFragment = this.retrievedLdesFragment.get();
		assertEquals(nrOfMembersAdded, actualFragment.getNrOfMembersAdded());
	}

	@Given("The following ldesFragments with relations")
	public void theFollowingLdesFragmentsWithRelations(List<FragmentWithRelation> fragments) {
		this.fragments = fragments.stream().map(FragmentWithRelation::fragment).toList();
	}

	@Then("the repository the following fragments with outgoing relations")
	public void theRepositoryTheFollowingFragmentsWithOutgoingRelations(
			List<OutgoingRelationResult> outgoingRelationResults) {
		outgoingRelationResults.forEach(outgoingRelationResult -> {
			List<Fragment> fragmentsByOutgoingRelation = fragmentRepository
					.retrieveFragmentsByOutgoingRelation(outgoingRelationResult.outgoingRelation);
			assertEquals(outgoingRelationResult.fragmentIds,
					fragmentsByOutgoingRelation.stream().map(Fragment::getFragmentId).collect(Collectors.toSet()));
		});

	}

	@When("I delete the fragment {string}")
	public void iDeleteTheFragment(String fragmentId) {
		fragmentRepository.retrieveFragment(LdesFragmentIdentifier.fromFragmentId(fragmentId))
				.map(Fragment::getFragmentId)
				.ifPresent(fragmentRepository::removeRelationsPointingToFragmentAndDeleteFragment);
	}

	@Then("The repository has the following fragments left")
	public void theRepositoryHasTheFollowingFragmentsLeft(List<FragmentWithRelation> fragmentWithRelations) {
		fragmentWithRelations.forEach(fragmentWithRelation -> {
			Optional<Fragment> fragment = fragmentRepository
					.retrieveFragment(fragmentWithRelation.fragment.getFragmentId());
			assertEquals(fragmentWithRelation.fragment.getRelations()
					.stream()
					.map(TreeRelation::treeNode)
					.collect(Collectors.toSet()),
					fragment
							.stream()
							.flatMap(fragment1 -> fragment1.getRelations().stream())
							.map(TreeRelation::treeNode)
							.collect(Collectors.toSet()));
		});
	}

	public record OutgoingRelationResult(LdesFragmentIdentifier outgoingRelation,
			Set<LdesFragmentIdentifier> fragmentIds) {
	}

	public record FragmentWithRelation(Fragment fragment) {
	}
}
