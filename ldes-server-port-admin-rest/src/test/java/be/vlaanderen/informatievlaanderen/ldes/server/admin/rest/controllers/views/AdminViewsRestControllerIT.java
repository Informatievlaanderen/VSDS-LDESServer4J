package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.views;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.SpringIntegrationTest;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/views")
public class AdminViewsRestControllerIT extends SpringIntegrationTest {

}
