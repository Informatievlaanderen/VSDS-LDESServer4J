package be.vlaanderen.informatievlaanderen.ldes.server;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/compaction")
public class CompactionIT extends LdesServerIntegrationTest {

}
