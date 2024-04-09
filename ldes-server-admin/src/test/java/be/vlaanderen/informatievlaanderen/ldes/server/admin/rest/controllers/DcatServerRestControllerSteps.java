package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.SpringIntegrationTest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DcatServerRestControllerSteps extends SpringIntegrationTest {
	private final String uuid = UUID.randomUUID().toString();
	private ResultActions resultActions;

	@Given("a db containing no dcatserver")
	public void aDbWithoutDcatserver() {
		when(dcatServerRepository.getServerDcat()).thenReturn(List.of());
	}

	@When("the client makes a post request with a valid model")
	public void theClientMakesAPostRequestWithAValidModel() throws Exception {
		when(dcatServerRepository.saveServerDcat(any()))
				.thenReturn(new DcatServer(uuid, readModelFromFile("dcat/catalog/valid-server-dcat.ttl")));

		resultActions = mockMvc.perform(post("/admin/api/v1/dcat")
				.contentType(Lang.TURTLE.getHeaderString())
				.content(readDataFromFile("dcat/catalog/valid-server-dcat.ttl")));
	}

	@And("the client receives an UUID")
	public void theClientReceivesAnUUID() throws Exception {
		resultActions.andExpect(content().string(uuid));
	}

	@Then("the client receives HTTP status {int} for dcatserverrequest")
	public void theClientReceivesHTTPStatusForDcatserverrequest(int status) throws Exception {
		resultActions.andExpect(status().is(status));
	}

	private byte[] readDataFromFile(String filename) throws IOException {
		Path path = ResourceUtils.getFile("classpath:" + filename).toPath();
		return Files.readAllBytes(path);
	}

	private Model readModelFromFile(String filename) {
		return RDFParser.source(filename).lang(Lang.TURTLE).toModel();
	}

	@When("the client makes a post request with a invalid model")
	public void theClientMakesAPostRequestWithAInvalidModel() throws Exception {
		resultActions = mockMvc.perform(post("/admin/api/v1/dcat")
				.contentType(Lang.TURTLE.getHeaderString())
				.content(readDataFromFile("dcat/catalog/invalid-server-dcat.ttl")));
	}

	@Given("a db containing one dcatserver")
	public void aDbContainingOneDcatserver() {
		DcatServer dcatServer = new DcatServer(uuid, readModelFromFile("dcat/catalog/valid-server-dcat.ttl"));
		when(dcatServerRepository.getServerDcat()).thenReturn(List.of(dcatServer));
		when(dcatServerRepository.getServerDcatById(uuid)).thenReturn(Optional.of(dcatServer));
	}

	@When("the client makes a put request with a valid model")
	public void theClientMakesAPutRequestWithAValidModel() throws Exception {
		resultActions = mockMvc.perform(put("/admin/api/v1/dcat/{id}", uuid)
				.contentType(Lang.TURTLE.getHeaderString())
				.content(readDataFromFile("dcat/catalog/valid-server-dcat.ttl")));
	}

	@When("the client makes a put request with a invalid model")
	public void theClientMakesAPutRequestWithAnInValidModel() throws Exception {
		resultActions = mockMvc.perform(put("/admin/api/v1/dcat/{id}", uuid)
				.contentType(Lang.TURTLE.getHeaderString())
				.content(readDataFromFile("dcat/catalog/invalid-server-dcat.ttl")));
	}

	@When("the client makes a delete serverdcat request")
	public void theClientMakesADeleteServerdcatRequest() throws Exception {
		resultActions = mockMvc.perform(delete("/admin/api/v1/dcat/{id}", uuid));
	}
}
