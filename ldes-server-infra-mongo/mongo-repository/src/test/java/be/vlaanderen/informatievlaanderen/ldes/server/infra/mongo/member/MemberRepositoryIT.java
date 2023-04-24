package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class MemberRepositoryIT extends SpringIntegrationTest {
}
