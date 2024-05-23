package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.PostgresFragmentationIntegrationTest;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/ldesfragment")
public class FragmentRepositoryIT extends PostgresFragmentationIntegrationTest {
}
