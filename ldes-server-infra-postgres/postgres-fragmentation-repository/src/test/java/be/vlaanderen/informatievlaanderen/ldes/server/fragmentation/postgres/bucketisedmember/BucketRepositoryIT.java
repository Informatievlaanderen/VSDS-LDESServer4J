package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.bucketisedmember;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.PostgresFragmentationIntegrationTest;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/bucket")
public class BucketRepositoryIT extends PostgresFragmentationIntegrationTest {
}
