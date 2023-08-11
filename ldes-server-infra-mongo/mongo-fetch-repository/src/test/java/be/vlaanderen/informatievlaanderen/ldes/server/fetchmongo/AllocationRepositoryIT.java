package be.vlaanderen.informatievlaanderen.ldes.server.fetchmongo;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class AllocationRepositoryIT extends MongoAllocationIntegrationTest {
}