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

import java.util.List;
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

	@Given("the database contains multiple dcatViews")
	public void theDatabaseContainsMultipleDcatViews() {
		Model model1 = RDFParser.source("features/view/dataservice.ttl").lang(Lang.TURTLE).build().toModel();
		Model model2 = RDFParser.source("features/view/dataservice2.ttl").lang(Lang.TURTLE).build().toModel();
		dcatViewMongoRepository.save(DcatView.from(new ViewName("col1", "view1"), model1));
		dcatViewMongoRepository.save(DcatView.from(new ViewName("col1", "view2"), model2));
	}

	@Then("I can find all dcatViews")
	public void iCanFindAllDcatViews() {
		List<DcatView> dcatViews = dcatViewMongoRepository.findAll();
		assertEquals(2, dcatViews.size());

		List<String> views = dcatViews.stream().map(DcatView::getViewName).map(ViewName::getViewName).toList();
		assertTrue(views.contains("view1"));
		assertTrue(views.contains("view2"));
	}
}
