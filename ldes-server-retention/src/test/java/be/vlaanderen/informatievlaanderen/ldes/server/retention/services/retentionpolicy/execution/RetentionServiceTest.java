package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.DeletionPolicyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.RetentionPolicyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
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
	private final MemberRemover memberRemover = mock(MemberRemover.class);
	private final RetentionPolicyCollection retentionPolicyCollection = mock(RetentionPolicyCollection.class);
	private final DeletionPolicyCollection deletionPolicyCollection = mock(DeletionPolicyCollection.class);
	private RetentionService retentionService;

	@BeforeEach
	void setUp() {
		retentionService = new RetentionService(memberPropertiesRepository, memberRemover, retentionPolicyCollection, deletionPolicyCollection);
	}

//	@Test
//	void when_MembersOfFragmentMatchRetentionPoliciesOfView_MembersAreRemovedFromTheView() {
//		MemberProperties firstMember = getMemberProperties("1", 0);
//		var timeBasedRetentionPolicy = new TimeBasedRetentionPolicy(Duration.ZERO);
//		when(memberPropertiesRepository.findExpiredMemberProperties(VIEW_A, timeBasedRetentionPolicy))
//				.thenReturn(Stream.of(firstMember));
//
//		var versionBasedRetentionPolicy = new VersionBasedRetentionPolicy(1);
//		MemberProperties secondMember = getMemberProperties("2", 1);
//		when(memberPropertiesRepository.findExpiredMemberProperties(VIEW_B, versionBasedRetentionPolicy))
//				.thenReturn(Stream.of(firstMember, secondMember));
//
//		var timeAndVersionBasedRetentionPolicy = new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 1);
//		MemberProperties thirdMember = getMemberProperties("3", 0);
//		when(memberPropertiesRepository.findExpiredMemberProperties(VIEW_C, timeAndVersionBasedRetentionPolicy))
//				.thenReturn(Stream.of(firstMember, secondMember, thirdMember));
//
//		when(retentionPolicyCollection.getRetentionPolicyMap()).thenReturn(Map.of(
//				VIEW_A, timeBasedRetentionPolicy,
//				VIEW_B, versionBasedRetentionPolicy,
//				VIEW_C, timeAndVersionBasedRetentionPolicy
//		));
//
//		when(deletionPolicyCollection.getEventSourceRetentionPolicyMap()).thenReturn(Map.of());
//
//		retentionService.executeRetentionPolicies();
//
//		verify(memberRemover).removeMemberFromView(firstMember, VIEW_A.asString());
//		verify(memberRemover).removeMemberFromView(firstMember, VIEW_B.asString());
//		verify(memberRemover).removeMemberFromView(secondMember, VIEW_B.asString());
//		verify(memberRemover).removeMemberFromView(firstMember, VIEW_C.asString());
//		verify(memberRemover).removeMemberFromView(secondMember, VIEW_C.asString());
//		verify(memberRemover).removeMemberFromView(thirdMember, VIEW_C.asString());
//		verify(memberRemover, never()).deleteMembers(anyList());
//	}

	@Test
	void when_MembersOfFragmentMatchRetentionPoliciesOfEventSource_MembersAreRemovedFromTheEventSource() {
//		MemberProperties firstMember = getMemberProperties("1", 0);
//		firstMember.addAllViewReferences(List.of(VIEW_A.getViewName()));
//		MemberProperties secondMember = getMemberProperties("2", 1);
//
//		var timeAndVersionBasedRetentionPolicy = new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 1);
//		when(memberPropertiesRepository.retrieveExpiredMembers(COLLECTION, timeAndVersionBasedRetentionPolicy))
//				.thenReturn(Stream.of(firstMember, secondMember));
//
//		when(deletionPolicyCollection.getEventSourceRetentionPolicyMap()).thenReturn(Map.of(
//				COLLECTION, timeAndVersionBasedRetentionPolicy
//		));
//
//		retentionService.executeRetentionPolicies();
//
//		verify(memberRemover).removeMembersFromEventSource(List.of(firstMember));
//		verify(memberRemover).deleteMembers(List.of(secondMember));
	}

	@Test
	void given_NoRetentionPolicies_when_RetentionPoliciesExecuted_then_DeleteNoMembers() {
		when(retentionPolicyCollection.getRetentionPolicyMap()).thenReturn(Map.of());

		retentionService.executeRetentionPolicies();

		verifyNoInteractions(memberRemover);
	}

//	private MemberProperties getMemberProperties(String memberId, int plusDays) {
//		return new MemberProperties(memberId, null, null, LocalDateTime.now().plusDays(plusDays), true);
//	}
}