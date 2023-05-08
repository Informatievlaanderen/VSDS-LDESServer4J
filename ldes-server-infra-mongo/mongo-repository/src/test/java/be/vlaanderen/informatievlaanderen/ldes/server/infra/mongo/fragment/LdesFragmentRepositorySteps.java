package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.SpringIntegrationTest;
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

public class LdesFragmentRepositorySteps extends SpringIntegrationTest {

	private List<LdesFragment> ldesFragments;
	private Optional<LdesFragment> retrievedLdesFragment;

	@DataTableType(replaceWithEmptyString = "[blank]")
	public LdesFragment ldesFragmentEntryTransformer(Map<String, String> row) {
		return new LdesFragment(
				ViewName.fromString(row.get("viewName")),
				row.get("fragmentPairs").equals("") ? List.of() : getFragmentPairs(row.get("fragmentPairs")),
				Boolean.parseBoolean(row.get("immutable")),
				null,
				Boolean.parseBoolean(row.get("softDeleted")),
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
	public void theFollowingLdesFragment(List<LdesFragment> ldesFragments) {
		this.ldesFragments = ldesFragments;
	}

	@When("I save the ldesFragments using the LdesFragmentRepository")
	public void iSaveTheLdesFragmentUsingTheLdesFragmentRepository() {
		ldesFragments.forEach(ldesFragment -> ldesFragmentMongoRepository.saveFragment(ldesFragment));
	}

	@Then("The ldesFragment with id {string} can be retrieved from the database")
	public void theLdesFragmentWithIdCanBeRetrievedFromTheDatabase(String fragmentId) {
		retrievedLdesFragment = ldesFragmentMongoRepository.retrieveFragment(fragmentId);
	}

	@Then("the repository contains {int} ldesFragments with viewname {string}")
	public void theRepositoryContainsLdesFragmentsWithViewname(int expectedNumberOfFragments, String viewName) {
		assertEquals(expectedNumberOfFragments, ldesFragmentMongoRepository.retrieveFragmentsOfView(viewName).count());
	}

	@And("The retrieved ldesFragment has the same properties as ldesFragment {int}")
	public void theRetrievedLdesFragmentHasTheSamePropertiesAsLdesFragment(int index) {
		assertTrue(retrievedLdesFragment.isPresent());
		LdesFragment actualLdesFragment = this.retrievedLdesFragment.get();
		LdesFragment expectedLdesFragment = this.ldesFragments.get(index - 1);
		assertEquals(expectedLdesFragment.getFragmentId(), actualLdesFragment.getFragmentId());
		assertEquals(expectedLdesFragment.getViewName(), actualLdesFragment.getViewName());
		assertEquals(expectedLdesFragment.getFragmentPairs(), actualLdesFragment.getFragmentPairs());
		assertEquals(expectedLdesFragment.isImmutable(), actualLdesFragment.isImmutable());
		assertEquals(expectedLdesFragment.getImmutableTimestamp(), actualLdesFragment.getImmutableTimestamp());
		assertEquals(expectedLdesFragment.isSoftDeleted(), actualLdesFragment.isSoftDeleted());
		assertEquals(expectedLdesFragment.getNumberOfMembers(), actualLdesFragment.getNumberOfMembers());
		assertEquals(expectedLdesFragment.getRelations(), actualLdesFragment.getRelations());
	}

	@When("I delete the ldesFragments of viewName {string}")
	public void iDeleteTheLdesFragmentsOfViewName(String viewName) {
		ldesFragmentMongoRepository.removeLdesFragmentsOfView(viewName);
	}
}
