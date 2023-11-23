package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.RetentionPolicyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class RetentionServiceTest {

	public static final ViewName VIEW = new ViewName("collection", "view");

	private final MemberPropertiesRepository memberPropertiesRepository = mock(MemberPropertiesRepository.class);
	private final MemberRemover memberRemover = mock(MemberRemover.class);
	private final RetentionPolicyCollection retentionPolicyCollection = mock(RetentionPolicyCollection.class);
	private RetentionService retentionService;

	@BeforeEach
	void setUp() {
		retentionService = new RetentionService(memberPropertiesRepository, memberRemover,
				retentionPolicyCollection);
	}

	@Test
	void when_MembersOfFragmentMatchRetentionPoliciesOfView_MembersAreDeleted() {
		when(retentionPolicyCollection.getRetentionPolicyMap())
				.thenReturn(Map.of(VIEW, List.of(new TimeBasedRetentionPolicy(Duration.ZERO))));
		MemberProperties firstMember = getMemberProperties("1", 0);
		MemberProperties secondMember = getMemberProperties("2", 1);
		MemberProperties thirdMember = getMemberProperties("3", 0);
		when(memberPropertiesRepository.getMemberPropertiesWithViewReference(VIEW))
				.thenReturn(Stream.of(firstMember, secondMember, thirdMember));

		retentionService.executeRetentionPolicies();

		InOrder inOrder = inOrder(retentionPolicyCollection, memberPropertiesRepository, memberRemover);
		inOrder.verify(retentionPolicyCollection).getRetentionPolicyMap();
		inOrder.verify(memberPropertiesRepository).getMemberPropertiesWithViewReference(VIEW);
		inOrder.verify(memberRemover).removeMemberFromView(firstMember, VIEW.asString());
		inOrder.verify(memberRemover).removeMemberFromView(thirdMember, VIEW.asString());
		inOrder.verifyNoMoreInteractions();
	}

	private MemberProperties getMemberProperties(String memberId, int plusDays) {
		return new MemberProperties(memberId, null, null, LocalDateTime.now().plusDays(plusDays));
	}

}