package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.collection.EventStreamCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@CucumberContextConfiguration
@EnableAutoConfiguration
@ActiveProfiles("test")
@ContextConfiguration(classes = { AdminEventStreamsRestController.class, AdminViewsRestController.class,
		AppConfig.class, PrefixAdderImpl.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.view",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.validation",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling" })
@Import(SpringIntegrationTest.EventStreamControllerTestConfiguration.class)
@SuppressWarnings("java:S2187")
public class SpringIntegrationTest {

	@Autowired
	public EventStreamCollection eventStreamCollection;
	@Autowired
	public ViewRepository viewRepository;
	@Autowired
	public ShaclShapeRepository shaclShapeRepository;

	@TestConfiguration
	public static class EventStreamControllerTestConfiguration {

		@MockBean
		public EventStreamCollection eventStreamCollection;
		@MockBean
		public ViewRepository viewRepository;
		@MockBean
		public ShaclShapeRepository shaclShapeRepository;
	}

}
