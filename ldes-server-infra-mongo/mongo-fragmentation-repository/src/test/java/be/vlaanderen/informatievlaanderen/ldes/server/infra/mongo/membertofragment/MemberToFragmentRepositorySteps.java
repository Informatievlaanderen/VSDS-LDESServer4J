package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.membertofragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.MongoFragmentationIntegrationTest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MemberToFragmentRepositorySteps extends MongoFragmentationIntegrationTest {

    private final Map<String, List<ViewName>> viewsPerCollection = new HashMap<>();

    @Given("I have the following collections")
    public void iHaveTheFollowingCollections(DataTable dataTable) {
        List<Map<String, String>> tableMap = dataTable.asMaps();
        tableMap.forEach(entry -> {
                    String collectionName = entry.get("collectionName");
                    List<String> views = List.of(entry.get("views").split(","));
                    List<ViewName> viewNames = views.stream().map(view -> new ViewName(collectionName, view)).toList();
                    viewsPerCollection.put(collectionName, viewNames);
                }
        );
    }

    @DataTableType(replaceWithEmptyString = "[blank]")
    public Member extractMember(Map<String, String> row) {
        return new Member(
                row.get("memberId"),
                ModelFactory.createDefaultModel(),
                Long.parseLong(row.get("sequenceNr"))
        );
    }

    @And("I create the following members")
    public void iCreateTheFollowingMembers(DataTable dataTable) {
        List<Map<String, String>> tableMap = dataTable.asMaps();
        tableMap.forEach(entry -> {
            List<ViewName> views = viewsPerCollection.get(entry.get("collectionName"));
            memberToFragmentRepository.create(views, extractMember(entry));
        });
    }

    @When("I request the next member for view {string}")
    public void iRequestTheNextMember(String viewName) {
        Optional<Member> nextMemberToFragment =
                memberToFragmentRepository.getNextMemberToFragment(ViewName.fromString(viewName));
    }
}
