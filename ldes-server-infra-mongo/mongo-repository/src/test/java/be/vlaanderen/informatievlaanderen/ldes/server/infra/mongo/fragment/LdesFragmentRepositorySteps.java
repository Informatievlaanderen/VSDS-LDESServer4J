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

	private LdesFragment ldesFragment;
	private Optional<LdesFragment> retrievedLdesFragment;

	@DataTableType
	public LdesFragment ldesFragmentEntryTransformer(Map<String, String> row) {
		return new LdesFragment(
				ViewName.fromString(row.get("viewName")),
				getFragmentPairs(row),
				Boolean.parseBoolean(row.get("immutable")),
				null,
				Boolean.parseBoolean(row.get("softDeleted")),
				Integer.parseInt(row.get("numberOfMembers")),
				List.of());
	}

	private List<FragmentPair> getFragmentPairs(Map<String, String> row) {
		String[] fragmentPairs = row.get("fragmentPairs").split(",");
		List<FragmentPair> fragmentPairList = new ArrayList<>();
		for (int i = 0; i < fragmentPairs.length; i += 2) {
			fragmentPairList.add(new FragmentPair(fragmentPairs[i], fragmentPairs[i + 1]));
		}
		return fragmentPairList;
	}

	@Given("The following ldesFragment")
	public void theFollowingLdesFragment(LdesFragment ldesFragment) {
		this.ldesFragment = ldesFragment;
	}

	@When("I save the ldesFragment using the LdesFragmentRepository")
	public void iSaveTheLdesFragmentUsingTheLdesFragmentRepository() {
		ldesFragmentMongoRepository.saveFragment(ldesFragment);
	}

	@Then("The ldesFragment with id {string} can be retrieved from the database")
	public void theLdesFragmentWithIdCanBeRetrievedFromTheDatabase(String fragmentId) {
		retrievedLdesFragment = ldesFragmentMongoRepository.retrieveFragment(fragmentId);
	}

	@And("The retrieved ldesFragment has the same properties the orignal ldesFragment")
	public void theRetrievedLdesFragmentHasTheSamePropertiesTheOrignalLdesFragment() {
		assertTrue(retrievedLdesFragment.isPresent());
		LdesFragment obaintedLdesFragment = this.retrievedLdesFragment.get();
		assertEquals(ldesFragment.getFragmentId(), obaintedLdesFragment.getFragmentId());
		assertEquals(ldesFragment.getViewName(), obaintedLdesFragment.getViewName());
		assertEquals(ldesFragment.getFragmentPairs(), obaintedLdesFragment.getFragmentPairs());
		assertEquals(ldesFragment.isImmutable(), obaintedLdesFragment.isImmutable());
		assertEquals(ldesFragment.getImmutableTimestamp(), obaintedLdesFragment.getImmutableTimestamp());
		assertEquals(ldesFragment.isSoftDeleted(), obaintedLdesFragment.isSoftDeleted());
		assertEquals(ldesFragment.getNumberOfMembers(), obaintedLdesFragment.getNumberOfMembers());
		assertEquals(ldesFragment.getRelations(), obaintedLdesFragment.getRelations());

	}
}
