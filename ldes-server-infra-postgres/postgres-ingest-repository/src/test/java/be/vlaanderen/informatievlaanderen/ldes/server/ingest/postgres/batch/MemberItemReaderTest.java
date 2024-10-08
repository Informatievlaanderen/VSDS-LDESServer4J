package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.EventStreamProperties;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.apache.jena.rdf.model.ModelFactory;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoConfiguration
@SpringBootTest
@TestExecutionListeners(listeners = {StepScopeTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
@AutoConfigureEmbeddedDatabase
@ActiveProfiles("postgres-test")
@ContextConfiguration(classes = {MemberItemReader.class})
class MemberItemReaderTest {
	private static final String SUBJECT_TEMPLATE = "http://test-data/mobility-hindrance/1/";
	private static final LocalDateTime START_TIME = LocalDateTime.now();
	private static final String COLLECTION_NAME = "mobility-hindrances";
	public static final EventStreamProperties EVENT_STREAM_PROPERTIES = new EventStreamProperties(
			COLLECTION_NAME,
			"http://purl.org/dc/terms/isVersionOf",
			"http://purl.org/dc/terms/created",
			false
	);
	private static final String VIEW_NAME = "by-page";
	private static final long COLLECTION_ID = 1;
	private static final long BY_PAGE_ID = 2;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private ItemReader<FragmentationMember> newMemberReader;

	@BeforeEach
	void setUp() {
		final String sql = """
				INSERT INTO collections
				VALUES (1, 'mobility-hindrances', 'http://purl.org/dc/terms/created', 'http://purl.org/dc/terms/isVersionOf', false,
				        false);
				
				INSERT INTO views VALUES (1, 1, 'by-page', '[]', '', 150);
				INSERT INTO views VALUES (2, 1, 'paged', '[]', '', 300);
				
				INSERT INTO buckets VALUES (1, '', 1);
				INSERT INTO buckets VALUES (2, '', 2);
				""";

		jdbcTemplate.update(sql);
	}

	@AfterEach
	void tearDown() {
		jdbcTemplate.update("DELETE FROM collections");
	}

	@Test
	void given_EmptyMembersTable_when_ReadNewMembers_then_ReturnNull() throws Exception {
		setupStepScope(COLLECTION_ID, COLLECTION_NAME, BY_PAGE_ID, VIEW_NAME);

		final FragmentationMember result = newMemberReader.read();

		assertThat(result).isNull();
	}

	@ParameterizedTest
	@ValueSource(strings = {"fantasy/non-existing", "mobility-hindrances/fantasy-view", "fantasy/by-page"})
	void given_AbsentCollectionsAndViews_when_RefragmentMembers_then_ReturnNull(String viewNameAsString) throws Exception {
		final ViewName viewName = ViewName.fromString(viewNameAsString);
		setupStepScope(10, viewName.getCollectionName(), 10, viewName.getViewName());

		final FragmentationMember result = newMemberReader.read();

		assertThat(result).isNull();
	}

	@Test
	void given_MembersPresentInDb_test_ReadNewMembers() throws Exception {
		int count = 5;
		setupStepScope(COLLECTION_ID, COLLECTION_NAME, BY_PAGE_ID, VIEW_NAME);
		insertMembers(count);

		final List<FragmentationMember> readMembers = new ArrayList<>();
		FragmentationMember member;
		do {
			member = newMemberReader.read();
			if (member != null) {
				readMembers.add(member);
			}

		} while (member != null);

		assertThat(readMembers)
				.hasSize(count)
				.doesNotHaveDuplicates()
				.first()
				.usingRecursiveComparison()
				.ignoringFields("model", "timestamp")
				.isEqualTo(expectedMember())
				.asInstanceOf(InstanceOfAssertFactories.type(FragmentationMember.class))
				.extracting("timestamp", InstanceOfAssertFactories.LOCAL_DATE_TIME)
				.isEqualToIgnoringNanos(START_TIME);
	}

	private void setupStepScope(long collectionId, String collectionName, long viewId, String viewName) {
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("collectionId", collectionId)
				.addLong("viewId", viewId)
				.addString("collectionName", collectionName)
				.addString("viewName", viewName)
				.toJobParameters();
		setupStepScope(jobParameters);
	}

	private void setupStepScope(JobParameters jobParameters) {
		StepExecution stepExecution = new StepExecution("step", new JobExecution(1L, jobParameters), 1L);
		StepSynchronizationManager.register(stepExecution);
		StepContext stepContext = new StepContext(stepExecution);
		stepContext.setAttribute("memberReader", newMemberReader);
	}

	private void insertMembers(int count) {
		final List<Object[]> batchArgs = IntStream.range(0, count)
				.mapToObj(this::mapToInsertAgs)
				.toList();

		jdbcTemplate.batchUpdate("INSERT INTO members VALUES (?, ?, ?, 1, true, '', ?, ?, 'http://test-data/mobility-hindrance/1')", batchArgs);
	}

	private Object[] mapToInsertAgs(int i) {
		final LocalDateTime timestamp = START_TIME.plusHours(i);
		final String subject = SUBJECT_TEMPLATE + (i + 1);
		final String oldId = COLLECTION_NAME + "/" + subject;

		return new Object[]{
				i + 1, subject, oldId, timestamp, UUID.randomUUID()
		};
	}

	private FragmentationMember expectedMember() {
		return new FragmentationMember(1, SUBJECT_TEMPLATE + (long) 1, "http://test-data/mobility-hindrance/1", START_TIME, EVENT_STREAM_PROPERTIES, ModelFactory.createDefaultModel());
	}


}