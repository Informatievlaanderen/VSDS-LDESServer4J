package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.PageMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies.ViewRetentionPolicyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.Duration;
import java.util.Map;

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
	private ViewRetentionPolicyCollection viewRetentionPolicyCollection;
	@Mock
	private MemberPropertiesRepository memberPropertiesRepository;
	@Mock
	private PageMemberRepository pageMemberRepository;
	@InjectMocks
	private ViewRetentionTask viewRetentionTask;

	@Test
	void when_MembersOfFragmentMatchRetentionPoliciesOfView_MembersAreRemovedFromTheView() {
		var timeBasedRetentionPolicy = new TimeBasedRetentionPolicy(Duration.ZERO);

		var versionBasedRetentionPolicy = new VersionBasedRetentionPolicy(1);

		var timeAndVersionBasedRetentionPolicy = new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 1);

		when(viewRetentionPolicyCollection.getRetentionPolicies()).thenReturn(Map.of(
				VIEW_A, timeBasedRetentionPolicy,
				VIEW_B, versionBasedRetentionPolicy,
				VIEW_C, timeAndVersionBasedRetentionPolicy
		));

		final RepeatStatus returnedRepeatStatus = viewRetentionTask.execute(mock(), mock());

		verify(memberPropertiesRepository).findExpiredMembers(VIEW_A, timeBasedRetentionPolicy);
		verify(memberPropertiesRepository).findExpiredMembers(VIEW_B, versionBasedRetentionPolicy);
		verify(memberPropertiesRepository).findExpiredMembers(VIEW_C, timeAndVersionBasedRetentionPolicy);
		verify(pageMemberRepository).deleteByViewNameAndMembersIds(eq(VIEW_A), anyList());
		verify(pageMemberRepository).deleteByViewNameAndMembersIds(eq(VIEW_B), anyList());
		verify(pageMemberRepository).deleteByViewNameAndMembersIds(eq(VIEW_C), anyList());
		assertThat(returnedRepeatStatus).isEqualTo(RepeatStatus.FINISHED);
	}

	@Test
	void given_NoRetentionPolicies_when_RetentionPoliciesExecuted_then_DeleteNoMembers() {
		when(viewRetentionPolicyCollection.getRetentionPolicies()).thenReturn(Map.of());

		final RepeatStatus returnedRepeatStatus = viewRetentionTask.execute(mock(), mock());

		verifyNoInteractions(pageMemberRepository);
		assertThat(returnedRepeatStatus).isEqualTo(RepeatStatus.FINISHED);
	}
}