package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatserver;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.entities.DcatServer;
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
	private DcatServer dcatServer;
	private DcatServer retrievedDcatServer;

	@Given("I have a serverDcat with an id and model")
	public void iHaveAServerDcatWithAnIdAndModel() {
		model = RDFParser.source("features/serverdcat/serverdcat.ttl").lang(Lang.TURTLE).build().toModel();
		dcatServer = new DcatServer(ID, model);
	}

	@Then("I can save the serverDcat with the repository")
	public void iCanSaveTheServerDcatWithTheRepository() {
		serverDcatMongoRepository.saveServerDcat(dcatServer);
	}

	@And("I can retrieve the serverDcat from the repository")
	public void iCanRetrieveTheServerDcatFromTheRepository() {
		Optional<DcatServer> optionalServerDcat = serverDcatMongoRepository.getServerDcatById(ID);
		assertTrue(optionalServerDcat.isPresent());
		retrievedDcatServer = optionalServerDcat.get();
	}

	@And("The retrieved serverDcat will be the same as the saved serverDcat")
	public void theRetrievedServerDcatWillBeTheSameAsTheSavedServerDcat() {
		assertEquals(dcatServer, retrievedDcatServer);
	}

	@Then("I can update the serverDcat with a new model")
	public void iCanUpdateTheServerDcatWithANewModel() {
		model = RDFParser.source("features/serverdcat/serverdcat-with-publisher.ttl").lang(Lang.TURTLE).build()
				.toModel();
		dcatServer = new DcatServer(ID, model);
		serverDcatMongoRepository.saveServerDcat(dcatServer);
	}

	@Then("I can delete the serverDcat")
	public void iCanDeleteTheServerDcat() {
		serverDcatMongoRepository.deleteServerDcat(ID);
	}

	@And("I can not retrieve the serverDcat from the repository")
	public void iCanNotRetrieveTheServerDcatFromTheRepository() {
		Optional<DcatServer> optionalServerDcat = serverDcatMongoRepository.getServerDcatById(ID);
		assertTrue(optionalServerDcat.isEmpty());
	}
}