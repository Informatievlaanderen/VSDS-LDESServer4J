package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.entities.Member;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class MemberCollectorImplTest {

	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private final MemberCollector memberCollector = new MemberCollectorImpl(memberRepository);

	@Disabled("To be enabled when snapshotting becomes functional again")
	@Test
	void when_MembersHaveMultipleVersionsOverFragments_TheyAreGrouped() {
		Fragment fragment = getLdesFragment("1");
		Fragment fragment2 = getLdesFragment("2");
		Fragment fragment3 = getLdesFragment("3");
		// when(memberRepository.getMembersByReference(fragment.getFragmentIdString()))
		// .thenReturn(getMemberStream());
		// when(memberRepository.getMembersByReference(fragment2.getFragmentIdString()))
		// .thenReturn(getMemberStream());
		// when(memberRepository.getMembersByReference(fragment3.getFragmentIdString()))
		// .thenReturn(getMemberStream());

		Map<String, List<Member>> membersGroupedByVersionOf = memberCollector
				.getMembersGroupedByVersionOf(List.of(fragment, fragment2, fragment3));

		assertEquals(Set.of("member1", "member2", "member3", "member4", "member5"), membersGroupedByVersionOf.keySet());
		assertEquals(3, membersGroupedByVersionOf.get("member1").size());
		assertEquals(3, membersGroupedByVersionOf.get("member2").size());
		assertEquals(3, membersGroupedByVersionOf.get("member3").size());
		assertEquals(3, membersGroupedByVersionOf.get("member4").size());
		assertEquals(3, membersGroupedByVersionOf.get("member5").size());
	}

	private Fragment getLdesFragment(String pageNumber) {
		return new Fragment(new LdesFragmentIdentifier(new ViewName("collectionName", "mobility-hindrances"),
				List.of(new FragmentPair("page", pageNumber))));
	}

	private Stream<Member> getMemberStream() {
		return Stream.of(getMember("member1"), getMember("member2"), getMember("member3"), getMember("member4"),
				getMember("member5"));
	}

	private Member getMember(String versionOf) {
		return new Member("", null, versionOf, LocalDateTime.now());
	}

}
