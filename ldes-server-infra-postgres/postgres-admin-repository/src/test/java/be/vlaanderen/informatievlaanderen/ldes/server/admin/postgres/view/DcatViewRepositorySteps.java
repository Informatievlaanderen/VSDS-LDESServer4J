package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.SpringIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class DcatViewRepositorySteps extends SpringIntegrationTest {

    private final static String COLLECTION_NAME = "collectionName";
    private final static String VIEW = "view";
    private final static ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, VIEW);
    private Model model;

    private DcatView dcatView;
    private DcatView resultDcatView;

    @Before
    public void teardown() {
        dcatViewPostgresRepository.findAll().stream().map(DcatView::getViewName).forEach(dcatViewPostgresRepository::delete);
    }

    @Given("I have a dcatView with a viewName and model")
    public void giveIHaveADcatView() {
        model = RDFParser.source("features/view/dataservice.ttl").lang(Lang.TURTLE).build().toModel();
        dcatView = DcatView.from(VIEW_NAME, model);
    }

    @Then("I can update the dcatView with a new model")
    public void iCanUpdateTheDcatViewWithANewModel() {
        model = RDFParser.source("features/view/dataservice2.ttl").lang(Lang.TURTLE).build().toModel();
        dcatView = DcatView.from(VIEW_NAME, model);
        dcatViewPostgresRepository.save(dcatView);
    }

    @And("I can save the dcatView with the repository")
    public void iSaveTheDcatViewWithTheRepository() {
        dcatViewPostgresRepository.save(dcatView);
    }

    @Then("I can retrieve the dcatView from the repository")
    public void iCanRetrieveTheDcatViewFromTheRepository() {
        Optional<DcatView> dcatViewOpt = dcatViewPostgresRepository.findByViewName(VIEW_NAME);
        assertThat(dcatViewOpt).isPresent();
        resultDcatView = dcatViewOpt.get();
    }

	@And("The retrieved dcatView will be the same as the saved dcatView")
	public void theRetrievedDcatViewWillBeTheSameAsTheSavedDcatView() {
        assertThat(dcatView.getDcat()).matches(actualDcat -> resultDcatView.getDcat().isIsomorphicWith(actualDcat));
        assertThat(dcatView.getViewName()).isEqualTo(resultDcatView.getViewName());
	}

    @Then("I can delete the dcatView")
    public void iCanDeleteTheDcatView() {
        dcatViewPostgresRepository.delete(VIEW_NAME);
    }

    @And("I can not retrieve the dcatView from the repository")
    public void iCanNotRetrieveTheDcatViewFromTheRepository() {
        Optional<DcatView> dcatViewOpt = dcatViewPostgresRepository.findByViewName(VIEW_NAME);
        assertThat(dcatViewOpt).isEmpty();
    }

    @Given("the database contains multiple dcatViews")
    public void theDatabaseContainsMultipleDcatViews() {
        Model model1 = RDFParser.source("features/view/dataservice.ttl").lang(Lang.TURTLE).build().toModel();
        Model model2 = RDFParser.source("features/view/dataservice2.ttl").lang(Lang.TURTLE).build().toModel();
        dcatViewPostgresRepository.save(DcatView.from(new ViewName("col1", "view1"), model1));
        dcatViewPostgresRepository.save(DcatView.from(new ViewName("col1", "view2"), model2));
    }

    @Then("I can find all dcatViews")
    public void iCanFindAllDcatViews() {
        List<DcatView> dcatViews = dcatViewPostgresRepository.findAll();

        assertThat(dcatViews)
                .hasSize(2)
                .map(DcatView::getViewName)
                .map(ViewName::getViewName)
                .containsExactlyInAnyOrder("view1", "view2");
    }

    @When("I delete the corresponding eventstream")
    public void iDeleteTheCorrespondingEventstream() {
        dcatViewPostgresRepository.deleteByCollectionName(COLLECTION_NAME);
    }

    @And("the database already contains another dcatView")
    public void theDatabaseAlreadyContainsAnotherDcatView() {
        final var otherDcatView = DcatView.from(new ViewName("other-" + COLLECTION_NAME, VIEW), ModelFactory.createDefaultModel());
        dcatViewPostgresRepository.save(otherDcatView);
    }

    @Then("the repository contains exactly {int} dcatViews")
    public void theRepositoryContainsExactlyDcatView(int expectedNumberOfDcatViews) {
        assertThat(dcatViewPostgresRepository.findAll()).hasSize(expectedNumberOfDcatViews);
    }
}
