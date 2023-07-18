package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.MongoFragmentationIntegrationTest;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/ldesfragment")
public class FragmentRepositoryIT extends MongoFragmentationIntegrationTest {
}
