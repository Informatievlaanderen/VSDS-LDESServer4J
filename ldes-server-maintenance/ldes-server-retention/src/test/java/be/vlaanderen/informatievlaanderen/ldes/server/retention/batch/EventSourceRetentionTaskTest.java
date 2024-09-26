package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.retentiontasklet.EventSourceRetentionTask;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution.MemberRemover;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventSourceRetentionTaskTest {
	private static final String COLLECTION = "collection";

	@Mock
	private StepExecution stepExecution;
	@Mock
	private ExecutionContext executionContext;
	@Mock
	private MemberPropertiesRepository memberPropertiesRepository;
	@Mock
	private MemberRemover memberRemover;
	@InjectMocks
	private EventSourceRetentionTask eventSourceRetentionTask;
	
	@BeforeEach
	void setUp() {
		when(stepExecution.getExecutionContext()).thenReturn(executionContext);
	}

	@Test
	void when_MembersOfFragmentMatchRetentionPoliciesOfEventSource_MembersAreRemovedFromTheEventSource() {
		MemberProperties firstMember = createMemberProperties(1L, 0, true);
		MemberProperties secondMember = createMemberProperties(2L, 1, false);

		var timeAndVersionBasedRetentionPolicy = new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 1);
		when(memberPropertiesRepository.retrieveExpiredMembers(COLLECTION, timeAndVersionBasedRetentionPolicy))
				.thenReturn(Stream.of(firstMember, secondMember));

		when(executionContext.get("retentionPolicy")).thenReturn(timeAndVersionBasedRetentionPolicy);
		when(executionContext.getString("name")).thenReturn(COLLECTION);

		final RepeatStatus returnedRepeatStatus = eventSourceRetentionTask.execute(mock(), new ChunkContext(new StepContext(stepExecution)));

		verify(memberRemover).removeMembersFromEventSource(List.of(firstMember));
		verify(memberRemover).deleteMembers(List.of(secondMember));
		assertThat(returnedRepeatStatus).isEqualTo(RepeatStatus.FINISHED);
	}

	@Test
	void given_NoRetentionPolicies_when_RetentionPoliciesExecuted_then_DeleteNoMembers() {
		final RepeatStatus returnedRepeatStatus = eventSourceRetentionTask.execute(mock(), new ChunkContext(new StepContext(stepExecution)));

		verifyNoInteractions(memberRemover);
		assertThat(returnedRepeatStatus).isEqualTo(RepeatStatus.FINISHED);
	}

	private MemberProperties createMemberProperties(long memberId, int plusDays, boolean inView) {
		return new MemberProperties(memberId, "coll", "http://ex.com",
				LocalDateTime.now().plusDays(plusDays), true, inView);
	}
}