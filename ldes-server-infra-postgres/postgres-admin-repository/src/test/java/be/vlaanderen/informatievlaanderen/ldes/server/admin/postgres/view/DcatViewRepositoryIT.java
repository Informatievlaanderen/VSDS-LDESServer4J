package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.SpringIntegrationTest;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/view")
public class DcatViewRepositoryIT extends SpringIntegrationTest {
}
