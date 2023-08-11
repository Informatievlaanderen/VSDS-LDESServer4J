package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.integrationtest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.DcatViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.integrationtest.stub.InMemoryAllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.services.TreeNodeFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.RecordApplicationEvents;

@CucumberContextConfiguration
@EnableAutoConfiguration
@RecordApplicationEvents
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication",
		"be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain" })
@ContextConfiguration(initializers = PropertyOverrideContextInitializer.class, classes = {
		InMemoryAllocationRepository.class })
@SuppressWarnings("java:S2187")
public class FetchIntegrationTest {

	@Autowired
	ApplicationEventPublisher applicationEventPublisher;
	@Autowired
	AllocationRepository allocationRepository;
	@Autowired
	TreeNodeFetcher treeNodeFetcher;

	@Autowired
	@MockBean
	FragmentRepository fragmentRepository;
	@Autowired
	@MockBean
	MemberRepository memberRepository;
	@Autowired
	@MockBean
	DcatViewService dcatViewService;

}
