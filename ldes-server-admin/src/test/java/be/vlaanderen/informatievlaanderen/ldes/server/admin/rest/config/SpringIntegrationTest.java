package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.AdminEventStreamsRestController;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.AdminServerDcatController;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.DcatDatasetRestController;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.DcatViewsRestController;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@ActiveProfiles("test")
@ContextConfiguration(classes = { AdminEventStreamsRestController.class, AdminServerDcatController.class,
		DcatViewsRestController.class, DcatDatasetRestController.class, PrefixAdderImpl.class })
@ComponentScan(value = {
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset",
		"be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation",
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
