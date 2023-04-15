package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.MemberIngestService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.converters.LdesMemberConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.exceptionhandling.IngestionRestResponseEntityExceptionHandler;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.event.EventPublishingTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberIngestionControllerSteps {

	private AppConfig appConfig;
	private LdesMemberIngestionController memberIngestionController;
	private ResultActions resultActions;

	@MockBean
	private MemberIngestService memberIngestService;

	private MockMvc mockMvc;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Given("a restcontroller for collection {string} and memberType {string}")
	public void aLdesMemberIngestionControllerForCollectionAndMemberType(String collectionName, String memberType)
			throws URISyntaxException, IOException {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setCollectionName(collectionName);
		ldesConfig.setMemberType(memberType);
		appConfig = new AppConfig();
		appConfig.setCollections(List.of(ldesConfig));

		memberIngestService = mock(MemberIngestService.class);

		memberIngestionController = new LdesMemberIngestionController(memberIngestService, appConfig);
		mockMvc = MockMvcBuilders.standaloneSetup(memberIngestionController)
				.setControllerAdvice(new IngestionRestResponseEntityExceptionHandler())
				.setMessageConverters(new LdesMemberConverter(appConfig))
				.build();

		EventPublishingTestExecutionListener publishingTestExecutionListener = new EventPublishingTestExecutionListener();

		Model shape = readModelFromFile("alternatives/example-shape-old.ttl", Lang.TURTLE);
		publisher.publishEvent(new ShaclChangedEvent(collectionName, shape));

	}

	@Then("Status {int} is returned")
	public void statusIsReturned(int status) throws Exception {
		resultActions.andExpect(status().is(status));
	}

	@And("I verify the new member is added")
	public void iVerifyTheNewMemberIsAdded() {
		verify(memberIngestService).addMember(any(Member.class));
	}

	@And("I verify the new member is not added")
	public void iVerifyTheNewMemberIsNotAdded() {
		verifyNoInteractions(memberIngestService);
	}

	private Model readModelFromFile(String fileName, Lang lang) throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		URI uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI();

		return RDFParserBuilder.create()
				.fromString(Files.lines(Paths.get(uri)).collect(Collectors.joining())).lang(lang)
				.toModel();
	}

	@When("I ingest a new member conform to shape {string}")
	public void iIngestANewMemberConformToShape(String fileName) throws Exception {
		Model member = readModelFromFile("alternatives/" + fileName, Lang.TURTLE);
		String memberString = RdfModelConverter.toString(member, Lang.NQUADS);
		resultActions = mockMvc.perform(post("/restaurant").contentType("application/n-quads").content(memberString));
	}
}
