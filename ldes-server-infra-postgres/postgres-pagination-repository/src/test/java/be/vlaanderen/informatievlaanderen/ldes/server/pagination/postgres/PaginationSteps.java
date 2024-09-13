package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PaginationSteps extends PostgresPaginationIntegrationTest {
	private int pageCount;
	@Given("I have {int} unpaged members for bucket {string}")
	public void iHaveUnpagedMembersForBucket(int memberCount, String bucket) {

	}

	@Given("I have a page with capacity of {int}")
	public void iHaveAPageWithCapacityOf(int capacity) {
	}

	@When("I assign the members to the page")
	public void iAssignTheMembersToThePage() {

	}

	@Then("I expect a new page is created")
	public void iExpectANewPageIsCreated() {

	}

	@Then("I expect no more unpaged members")
	public void iExpectNoMoreUnpagedMembers() {

	}

	@Given("I have a mutable Page")
	public void iHaveAMutablePage() {

	}

	@When("I create a page given the mutable page")
	public void iCreateAPageGivenTheMutablePage() {

	}

	@And("The old page has a generic relation to the new page")
	public void theOldPageHasAGenericRelationToTheNewPage() {

	}
}
