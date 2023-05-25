package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.entities.ServerDcat;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.SpringIntegrationTest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerDcatRepositorySteps extends SpringIntegrationTest {
	private static final String ID = "55bda29b-65b5-4c49-88db-b592d08ec9ff";
	private Model model;
	private ServerDcat serverDcat;
	private ServerDcat retrievedServerDcat;

	@Given("I have a serverDcat with an id and model")
	public void iHaveAServerDcatWithAnIdAndModel() {
		model = RDFParser.source("features/serverdcat/serverdcat.ttl").lang(Lang.TURTLE).build().toModel();
		serverDcat = new ServerDcat(ID, model);
	}

	@Then("I can save the serverDcat with the repository")
	public void iCanSaveTheServerDcatWithTheRepository() {
		serverDcatMongoRepository.save(serverDcat);
	}

	@And("I can retrieve the serverDcat from the repository")
	public void iCanRetrieveTheServerDcatFromTheRepository() {
		Optional<ServerDcat> optionalServerDcat = serverDcatMongoRepository.findById(ID);
		assertTrue(optionalServerDcat.isPresent());
		retrievedServerDcat = optionalServerDcat.get();
	}

	@And("The retrieved serverDcat will be the same as the saved serverDcat")
	public void theRetrievedServerDcatWillBeTheSameAsTheSavedServerDcat() {
		assertEquals(serverDcat, retrievedServerDcat);
	}

	@Then("I can update the serverDcat with a new model")
	public void iCanUpdateTheServerDcatWithANewModel() {
		model = RDFParser.source("features/serverdcat/serverdcat-with-publisher.ttl").lang(Lang.TURTLE).build().toModel();
		serverDcat = new ServerDcat(ID, model);
		serverDcatMongoRepository.save(serverDcat);
	}

	@Then("I can delete the serverDcat")
	public void iCanDeleteTheServerDcat() {
		serverDcatMongoRepository.deleteById(ID);
	}

	@And("I can not retrieve the serverDcat from the repository")
	public void iCanNotRetrieveTheServerDcatFromTheRepository() {
		Optional<ServerDcat> optionalServerDcat = serverDcatMongoRepository.findById(ID);
		assertTrue(optionalServerDcat.isEmpty());
	}
}
