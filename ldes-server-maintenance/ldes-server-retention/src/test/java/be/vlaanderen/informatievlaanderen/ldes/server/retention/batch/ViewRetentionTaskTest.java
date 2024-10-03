package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.retentiontasklet.ViewRetentionTask;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.PageMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewRetentionTaskTest {
	private static final String COLLECTION = "collection";
	private static final ViewName VIEW_A = new ViewName(COLLECTION, "viewA");
	private static final ViewName VIEW_B = new ViewName(COLLECTION, "viewB");
	private static final ViewName VIEW_C = new ViewName(COLLECTION, "viewC");

	@Mock
	private MemberPropertiesRepository memberPropertiesRepository;
	@Mock
	private PageMemberRepository pageMemberRepository;
	@Mock
	private StepExecution stepExecution;
	@Mock
	private ExecutionContext executionContext;
	@InjectMocks
	private ViewRetentionTask viewRetentionTask;

	@BeforeEach
	void setUp() {
		when(stepExecution.getExecutionContext()).thenReturn(executionContext);
	}

	@Test
	void when_MembersOfFragmentMatchRetentionPoliciesOfView_MembersAreRemovedFromTheView() {
		var timeBasedRetentionPolicy = new TimeBasedRetentionPolicy(Duration.ZERO);

		var versionBasedRetentionPolicy = new VersionBasedRetentionPolicy(1);

		var timeAndVersionBasedRetentionPolicy = new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 1);

		when(executionContext.get("retentionPolicy"))
				.thenReturn(timeBasedRetentionPolicy)
				.thenReturn(versionBasedRetentionPolicy)
				.thenReturn(timeAndVersionBasedRetentionPolicy);
		when(executionContext.getString("name"))
				.thenReturn(VIEW_A.asString())
				.thenReturn(VIEW_B.asString())
				.thenReturn(VIEW_C.asString());

		for (int i = 0; i < 3; i++) {
			viewRetentionTask.execute(mock(), new ChunkContext(new StepContext(stepExecution)));
		}

		verify(memberPropertiesRepository).findExpiredMembers(VIEW_A, timeBasedRetentionPolicy);
		verify(memberPropertiesRepository).findExpiredMembers(VIEW_B, versionBasedRetentionPolicy);
		verify(memberPropertiesRepository).findExpiredMembers(VIEW_C, timeAndVersionBasedRetentionPolicy);
		verify(pageMemberRepository).deleteByViewNameAndMembersIds(eq(VIEW_A), anyList());
		verify(pageMemberRepository).deleteByViewNameAndMembersIds(eq(VIEW_B), anyList());
		verify(pageMemberRepository).deleteByViewNameAndMembersIds(eq(VIEW_C), anyList());
	}

	@Test
	void given_NoRetentionPolicies_when_RetentionPoliciesExecuted_then_DeleteNoMembers() {
		final RepeatStatus returnedRepeatStatus = viewRetentionTask.execute(mock(), new ChunkContext(new StepContext(stepExecution)));

		verifyNoInteractions(pageMemberRepository);
		assertThat(returnedRepeatStatus).isEqualTo(RepeatStatus.FINISHED);
	}
}