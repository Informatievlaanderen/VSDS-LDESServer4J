package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
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

public class DcatViewRepositorySteps extends SpringIntegrationTest {

	private final static String COLLECTION_NAME = "collectionName";
	private final static String VIEW = "view";
	private final static ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, VIEW);
	private Model model;

	private DcatView dcatView;
	private DcatView resultDcatView;

	@Given("I have a dcatView with a viewName and model")
	public void giveIHaveADcatView() {
		model = RDFParser.source("features/view/dataservice.ttl").lang(Lang.TURTLE).build().toModel();
		dcatView = DcatView.from(VIEW_NAME, model);
	}

	@Then("I can update the dcatView with a new model")
	public void iCanUpdateTheDcatViewWithANewModel() {
		model = RDFParser.source("features/view/dataservice2.ttl").lang(Lang.TURTLE).build().toModel();
		dcatView = DcatView.from(VIEW_NAME, model);
		dcatViewMongoRepository.save(dcatView);
	}

	@And("I can save the dcatView with the repository")
	public void iSaveTheDcatViewWithTheRepository() {
		dcatViewMongoRepository.save(dcatView);
	}

	@Then("I can retrieve the dcatView from the repository")
	public void iCanRetrieveTheDcatViewFromTheRepository() {
		Optional<DcatView> dcatViewOpt = dcatViewMongoRepository.findByViewName(VIEW_NAME);
		assertTrue(dcatViewOpt.isPresent());
		resultDcatView = dcatViewOpt.get();
	}

	@And("The retrieved dcatView will be the same as the saved dcatView")
	public void theRetrievedDcatViewWillBeTheSameAsTheSavedDcatView() {
		assertTrue(dcatView.getDcat().isIsomorphicWith(resultDcatView.getDcat()));
		assertEquals(dcatView.getViewName(), resultDcatView.getViewName());
	}

	@Then("I can delete the dcatView")
	public void iCanDeleteTheDcatView() {
		dcatViewMongoRepository.delete(VIEW_NAME);
	}

	@And("I can not retrieve the dcatView from the repository")
	public void iCanNotRetrieveTheDcatViewFromTheRepository() {
		Optional<DcatView> dcatViewOpt = dcatViewMongoRepository.findByViewName(VIEW_NAME);
		assertTrue(dcatViewOpt.isEmpty());
	}

}
