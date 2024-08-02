package be.vlaanderen.informatievlaanderen.ldes.server;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.test.context.jdbc.Sql;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/compaction")
public class CompactionIT extends LdesServerIntegrationTest {

}
