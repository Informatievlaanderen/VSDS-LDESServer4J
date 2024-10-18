package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/eventstreams")
public class AdminEventStreamsRestControllerIT {

}
