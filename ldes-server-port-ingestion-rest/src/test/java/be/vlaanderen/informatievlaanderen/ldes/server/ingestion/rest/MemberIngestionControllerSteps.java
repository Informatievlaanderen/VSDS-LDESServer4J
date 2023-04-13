package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.repository.LdesConfigRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.MemberIngestService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class MemberIngestionControllerSteps {
    private LdesConfig ldesConfig;

    private LdesConfigRepository ldesConfigRepository;
    private LdesConfigModelService ldesConfigModelService;
    private MemberIngestService memberIngestService;
    private LdesMemberIngestionController memberIngestionController;


    @Autowired
    private MockMvc mockMvc;

    @Given("A ldes configuration")
    public void createConfiguration() {
        ldesConfig = new LdesConfig();
        ldesConfig.setCollectionName("restaurant");
        ldesConfig.setMemberType("http://example.com/restaurant#MenuItem");
    }

    @And("A MemberIngestionService and LdesConfigModelService")
    public void aMemberIngestionServiceAndLdesConfigModelService() {
        ldesConfigRepository = mock(LdesConfigRepository.class);
        ldesConfigModelService = new LdesConfigModelServiceImpl(ldesConfigRepository);

        memberIngestService = mock(MemberIngestService.class);
    }

    @Then("I create a MemberIngestionController")
    public void iCreateAMemberIngestionController() {
        memberIngestionController = new LdesMemberIngestionController(memberIngestService, ldesConfigModelService, ldesConfig);
        mockMvc = MockMvcBuilders.standaloneSetup(memberIngestionController).build();

    }

    @Given("a LdesMemberIngestionController for collection {string} and memberType {string}")
    public void aLdesMemberIngestionControllerForCollectionAndMemberType(String collectionName, String memberType) {
        ldesConfig = new LdesConfig();
        ldesConfig.setCollectionName(collectionName);
        ldesConfig.setMemberType(memberType);
    }

    @When("I ingest a new member with the old shape")
    public void iIngestANewMemberWithTheOldShape() throws Exception {
        Model member = readModelFromFile("alternatives/example-data-old.ttl", Lang.TURTLE);
        String memberString = RdfModelConverter.toString(member, Lang.NQUADS);
        mockMvc.perform(post("/restaurant").contentType("application/n-quads").content(memberString));
    }

    @Then("Status {int} is returned")
    public void statusIsReturned(int status) {
        // responseSpec.expectStatus().isEqualTo(status);
    }

    @When("I ingest a new member with the new shape")
    public void iIngestANewMemberWithTheNewShape() {
    }

    @When("I put the new shape to the server")
    public void iPutTheNewShapeToTheServer() {
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

}
