package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.DeletionPolicyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.PageMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.RetentionPolicyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class RetentionServiceTest {

	public static final String COLLECTION = "collection";
	public static final ViewName VIEW_A = new ViewName(COLLECTION, "viewA");
	public static final ViewName VIEW_B = new ViewName(COLLECTION, "viewB");
	public static final ViewName VIEW_C = new ViewName(COLLECTION, "viewC");

	private final MemberPropertiesRepository memberPropertiesRepository = mock(MemberPropertiesRepository.class);
	private final PageMemberRepository pageMemberRepository = mock(PageMemberRepository.class);
	private final MemberRemover memberRemover = mock(MemberRemover.class);
	private final RetentionPolicyCollection retentionPolicyCollection = mock(RetentionPolicyCollection.class);
	private final DeletionPolicyCollection deletionPolicyCollection = mock(DeletionPolicyCollection.class);
	private RetentionService retentionService;

	@BeforeEach
	void setUp() {
		retentionService = new RetentionService(memberPropertiesRepository, pageMemberRepository, memberRemover, retentionPolicyCollection, deletionPolicyCollection);
	}

	@Test
	void when_MembersOfFragmentMatchRetentionPoliciesOfView_MembersAreRemovedFromTheView() {
		var timeBasedRetentionPolicy = new TimeBasedRetentionPolicy(Duration.ZERO);

		var versionBasedRetentionPolicy = new VersionBasedRetentionPolicy(1);

		var timeAndVersionBasedRetentionPolicy = new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 1);

		when(retentionPolicyCollection.getRetentionPolicyMap()).thenReturn(Map.of(
				VIEW_A, timeBasedRetentionPolicy,
				VIEW_B, versionBasedRetentionPolicy,
				VIEW_C, timeAndVersionBasedRetentionPolicy
		));

		when(deletionPolicyCollection.getEventSourceRetentionPolicyMap()).thenReturn(Map.of());

		retentionService.executeRetentionPolicies();

		verify(memberPropertiesRepository).findExpiredMembers(VIEW_A, timeBasedRetentionPolicy);
		verify(memberPropertiesRepository).findExpiredMembers(VIEW_B, versionBasedRetentionPolicy);
		verify(memberPropertiesRepository).findExpiredMembers(VIEW_C, timeAndVersionBasedRetentionPolicy);
	}

	@Test
	void when_MembersOfFragmentMatchRetentionPoliciesOfEventSource_MembersAreRemovedFromTheEventSource() {
		MemberProperties firstMember = getMemberProperties(1l, 0);
		MemberProperties secondMember = new MemberProperties(2L, "coll", "http://ex.com",
				LocalDateTime.now().plusDays(1), true, false);

		var timeAndVersionBasedRetentionPolicy = new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 1);
		when(memberPropertiesRepository.retrieveExpiredMembers(COLLECTION, timeAndVersionBasedRetentionPolicy))
				.thenReturn(Stream.of(firstMember, secondMember));

		when(deletionPolicyCollection.getEventSourceRetentionPolicyMap()).thenReturn(Map.of(
				COLLECTION, timeAndVersionBasedRetentionPolicy
		));

		retentionService.executeRetentionPolicies();

		verify(memberRemover).removeMembersFromEventSource(List.of(firstMember));
		verify(memberRemover).deleteMembers(List.of(secondMember));
	}

	@Test
	void given_NoRetentionPolicies_when_RetentionPoliciesExecuted_then_DeleteNoMembers() {
		when(retentionPolicyCollection.getRetentionPolicyMap()).thenReturn(Map.of());

		retentionService.executeRetentionPolicies();

		verifyNoInteractions(memberRemover);
	}

	private MemberProperties getMemberProperties(long memberId, int plusDays) {
		return new MemberProperties(memberId, "coll", "http://ex.com",
				LocalDateTime.now().plusDays(plusDays), true, true);
	}
}