package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.sequence;// package

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.MongoFragmentationIntegrationTest;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/sequence")
public class FragmentSequenceRepositoryIT extends MongoFragmentationIntegrationTest {
}
