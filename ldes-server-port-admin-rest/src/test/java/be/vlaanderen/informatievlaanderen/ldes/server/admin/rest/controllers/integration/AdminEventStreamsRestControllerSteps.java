package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.integration;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.AdminEventStreamsRestController;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@CucumberContextConfiguration
@EnableAutoConfiguration
@ActiveProfiles("test")
@ContextConfiguration(classes = {AdminEventStreamsRestController.class, AppConfig.class})
@ComponentScan(value = {"be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.view",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.validation",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling"})
public class AdminEventStreamsRestControllerSteps {
	private ResultActions resultActions;
	@MockBean
	private EventStreamRepository eventStreamRepository;
	@MockBean
	private ViewRepository viewRepository;
	@MockBean
	private ShaclShapeRepository shaclShapeRepository;
	@Autowired
	private MockMvc mockMvc;

	@Given("a db containing multiple eventstreams")
	public void aDbContainingMultipleEventstreams() {
		when(eventStreamRepository.retrieveAllEventStreams()).thenReturn(List.of());
		when(viewRepository.retrieveAllViewsOfCollection(anyString())).thenReturn(List.of());
		when(shaclShapeRepository.retrieveShaclShape(anyString())).thenReturn(Optional.empty());
	}

	@When("the client calls {string}")
	public void theClientCallsAdminApiVEventStreams(String url) throws Exception {
		resultActions = mockMvc.perform(get(url).accept(MediaType.valueOf("text/turtle")));
	}

	@Then("the client receives list of event streams")
	public void theClientReceivesListOfEventStreams() throws Exception {
		resultActions.andExpect(status().isOk());
	}
}
