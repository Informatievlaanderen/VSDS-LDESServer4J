package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.sequence;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentSequence;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.MongoFragmentationIntegrationTest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FragmentSequenceRepositorySteps extends MongoFragmentationIntegrationTest {

	private Optional<FragmentSequence> retrieveFragmentSequence;

	@When("I save a new FragmentSequence for view {string} with sequenceNr {int}")
	public void iSaveANewFragmentSequenceForViewWithSequenceNr(String viewName, int sequenceNr) {
		FragmentSequence fragmentSequence = new FragmentSequence(ViewName.fromString(viewName), sequenceNr);
		fragmentSequenceRepository.saveLastProcessedSequence(fragmentSequence);
	}

	@And("I request the last processed sequence for view {string}")
	public void iRequestTheLastProcessedSequenceForView(String viewName) {
		retrieveFragmentSequence = fragmentSequenceRepository.findLastProcessedSequence(ViewName.fromString(viewName));
	}

	@Then("I receive a FragmentSequence with sequenceNr {int}")
	public void iReceiveAFragmentSequenceWithSequenceNr(int sequenceNr) {
		assertTrue(retrieveFragmentSequence.isPresent());
		assertEquals(sequenceNr, retrieveFragmentSequence.get().sequenceNr());
	}

	@When("I delete the sequence for view {string}")
	public void iDeleteTheSequenceForView(String viewName) {
		fragmentSequenceRepository.deleteByViewName(ViewName.fromString(viewName));
	}

	@Then("I do not find a FragmentSequence")
	public void iDoNotFindAFragmentSequence() {
		assertTrue(retrieveFragmentSequence.isEmpty());
	}

	@When("I delete the sequence for collection {string}")
	public void iDeleteTheSequenceForCollection(String collectionName) {
		fragmentSequenceRepository.deleteByCollection(collectionName);
	}
}
