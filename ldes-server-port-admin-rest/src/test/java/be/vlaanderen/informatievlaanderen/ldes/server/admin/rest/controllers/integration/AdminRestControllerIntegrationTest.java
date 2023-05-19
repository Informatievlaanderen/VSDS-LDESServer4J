package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.integration;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.repository.SnapshotRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@CucumberContextConfiguration
@EnableAutoConfiguration
@ActiveProfiles("test")
@ComponentScan(value = {"be.vlaanderen.informatievlaanderen.ldes.server"})
@SuppressWarnings("java:S2187")
public class AdminRestControllerIntegrationTest {
	static ResultActions latestResultActions;

	@MockBean
	private EventStreamRepository eventStreamRepository;
	@MockBean
	private ViewRepository viewRepository;
	@MockBean
	private ShaclShapeRepository shaclShapeRepository;
	@MockBean
	private SnapshotRepository snapshotRepository;
	@MockBean
	private MemberRepository memberRepository;
	@MockBean
	private LdesFragmentRepository ldesFragmentRepository;

	@Autowired
	private MockMvc mockMvc;

	protected void executeGet(String url) throws Exception {
		 latestResultActions = mockMvc.perform(get(url));
	}


}
