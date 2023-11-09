package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class FragmentDeletionIT extends FragmentDeletionIntegrationTest {
}
