package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.AdminEventStreamsRestController;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.AdminServerDcatController;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.DcatDatasetRestController;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.DcatViewsRestController;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@CucumberContextConfiguration
@EnableAutoConfiguration
@ActiveProfiles({ "test", "rest" })
@ContextConfiguration(classes = { AdminEventStreamsRestController.class, AdminServerDcatController.class,
		DcatViewsRestController.class, DcatDatasetRestController.class, PrefixAdderImpl.class })
@ComponentScan(value = {
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.view",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset",
		"be.vlaanderen.informatievlaanderen.ldes.server.domain.validation",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling" })
@SuppressWarnings("java:S2187")
public class SpringIntegrationTest {

	@Autowired
	@MockBean
	protected DcatDatasetRepository dcatDatasetRepository;

	@Autowired
	@MockBean
	protected DcatViewRepository dcatViewRepository;

	@Autowired
	@MockBean
	protected DcatServerRepository dcatServerRepository;

	@Autowired
	@MockBean
	protected EventStreamRepository eventStreamRepository;

	@Autowired
	@MockBean
	protected ViewRepository viewRepository;

	@Autowired
	@MockBean
	protected ShaclShapeRepository shaclShapeRepository;

	@Autowired
	protected ApplicationEventPublisher eventPublisher;

	@Autowired
	protected MockMvc mockMvc;

}
