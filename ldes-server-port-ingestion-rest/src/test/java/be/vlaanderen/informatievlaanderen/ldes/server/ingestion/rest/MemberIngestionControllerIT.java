package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.config.IngestionWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.exceptionhandling.IngestionRestResponseEntityExceptionHandler;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@WebMvcTest
@ActiveProfiles("test")
@ContextConfiguration(classes = { LdesMemberIngestionController.class,
		IngestionWebConfig.class, AppConfig.class, IngestionRestResponseEntityExceptionHandler.class })
public class MemberIngestionControllerIT {
}
