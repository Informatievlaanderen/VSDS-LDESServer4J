package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.AdminEventStreamsRestController;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.AdminServerDcatController;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.collection.EventStreamCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@CucumberContextConfiguration
@EnableAutoConfiguration
@ActiveProfiles("test")
@ContextConfiguration(classes = { AdminEventStreamsRestController.class, AdminServerDcatController.class,
		AppConfig.class, PrefixAdderImpl.class })
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.view",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.validation",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling" })
@SuppressWarnings("java:S2187")
public class SpringIntegrationTest {
	@MockBean
	public DcatViewRepository dcatViewRepository;
	@MockBean
	@Autowired
	public DcatServerRepository dcatServerRepository;
	@MockBean
	@Autowired
	public EventStreamCollection eventStreamRepository;
	@MockBean
	@Autowired
	public ViewRepository viewRepository;
	@MockBean
	@Autowired
	public ShaclShapeRepository shaclShapeRepository;
	@Autowired
	public MockMvc mockMvc;

}
