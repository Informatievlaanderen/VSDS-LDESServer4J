package be.vlaanderen.informatievlaanderen.ldes.server.compaction;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.mockito.Mockito.when;

public class CompactionServiceSteps extends CompactionIntegrationTest {

    @DataTableType
    public ViewSpecification ViewSpecificationEntryTransformer(Map<String, String> row) {
        return new ViewSpecification(
                ViewName.fromString(row.get("viewName")),
                List.of(), List.of(), Integer.parseInt(row.get("pageSize")));
    }

    @DataTableType(replaceWithEmptyString = "[blank]")
    public Fragment FragmentEntryTransformer(Map<String, String> row) {
        return new Fragment(
                LdesFragmentIdentifier.fromFragmentId(row.get("fragmentIdentifier")),
                Boolean.parseBoolean(row.get("immutable")), 0, row.get("relation").equals("") ? List.of() : List.of(new TreeRelation("", LdesFragmentIdentifier.fromFragmentId(row.get("relation")), "", "", GENERIC_TREE_RELATION)));
    }

    @Given("a view with the following properties")
    public void aViewWithTheFollowingProperties(ViewSpecification viewSpecification) {
        applicationEventPublisher.publishEvent(new ViewAddedEvent(viewSpecification));
    }

    @And("the following Fragments are available")
    public void theFollowingFragmentsAreAvailable(List<Fragment>fragments) {
        fragments.forEach(fragment -> {
            when(fragmentRepository.retrieveFragment(fragment.getFragmentId())).thenReturn(Optional.of(fragment));
        });
    }

    @Then("wait sometime")
    public void waitSometime() throws InterruptedException {
        Thread.sleep(1000);
    }
}
