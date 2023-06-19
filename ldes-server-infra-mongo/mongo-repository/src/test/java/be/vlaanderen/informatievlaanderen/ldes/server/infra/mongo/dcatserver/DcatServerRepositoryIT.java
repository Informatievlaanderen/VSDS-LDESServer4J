package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatserver;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.SpringIntegrationTest;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/serverdcat")
public class DcatServerRepositoryIT extends SpringIntegrationTest {
}