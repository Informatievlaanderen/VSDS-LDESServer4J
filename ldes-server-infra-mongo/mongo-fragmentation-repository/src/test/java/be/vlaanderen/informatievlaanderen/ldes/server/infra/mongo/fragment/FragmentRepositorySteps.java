package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.MongoFragmentationIntegrationTest;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FragmentRepositorySteps extends MongoFragmentationIntegrationTest {

	private List<Fragment> fragments;
	private Optional<Fragment> retrievedLdesFragment;

	@DataTableType(replaceWithEmptyString = "[blank]")
	public Fragment ldesFragmentEntryTransformer(Map<String, String> row) {
		return new Fragment(new LdesFragmentIdentifier(
				ViewName.fromString(row.get("viewName")),
				row.get("fragmentPairs").equals("") ? List.of() : getFragmentPairs(row.get("fragmentPairs"))),
				Boolean.parseBoolean(row.get("immutable")),
				Integer.parseInt(row.get("numberOfMembers")),
				List.of());
	}

	private List<FragmentPair> getFragmentPairs(String row) {
		String[] fragmentPairs = row.split(",");
		List<FragmentPair> fragmentPairList = new ArrayList<>();
		for (int i = 0; i < fragmentPairs.length; i += 2) {
			fragmentPairList.add(new FragmentPair(fragmentPairs[i], fragmentPairs[i + 1]));
		}
		return fragmentPairList;
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
		assertEquals(expectedFragment.getNumberOfMembers(), actualFragment.getNumberOfMembers());
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
}
