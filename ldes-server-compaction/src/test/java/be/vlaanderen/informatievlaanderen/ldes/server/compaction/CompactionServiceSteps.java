package be.vlaanderen.informatievlaanderen.ldes.server.compaction;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;

import java.util.List;
import java.util.Map;

public class CompactionServiceSteps extends CompactionIntegrationTest {

	@DataTableType
	public ViewSpecification ViewSpecificationEntryTransformer(Map<String, String> row) {
		return new ViewSpecification(
				ViewName.fromString(row.get("viewName")),
				List.of(), List.of(), Integer.parseInt(row.get("pageSize")));
	}

	@Given("a view with the following properties")
	public void aViewWithTheFollowingProperties(ViewSpecification viewSpecification) {
		applicationEventPublisher.publishEvent(new ViewAddedEvent(viewSpecification));
	}

}
