package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import io.micrometer.observation.Observation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class FragmentationStrategyImplTest {
    private static final String MEMBER_ID = "memberId";
    private static final long SEQ_NR = 5L;
    private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
    private static final LdesFragmentIdentifier FRAGMENT_ID = new LdesFragmentIdentifier(VIEW_NAME, List.of());
    private final MockedBucketisedMemberSaver bucketisedMemberSaver = new MockedBucketisedMemberSaver();
    private final FragmentationStrategyImpl fragmentationStrategy = new FragmentationStrategyImpl(bucketisedMemberSaver);

    @Test
    void when_memberIsAddedToFragment_FragmentationStrategyImplSavesUpdatedFragment() {
        final Fragment fragment = new Fragment(FRAGMENT_ID);
        final Member member = new Member(MEMBER_ID, null, SEQ_NR);
        final BucketisedMember expectedBucketisedMember = new BucketisedMember(MEMBER_ID, VIEW_NAME, FRAGMENT_ID.asDecodedFragmentId(), SEQ_NR);
        fragmentationStrategy.addMemberToBucket(fragment, member, mock(Observation.class));
        final List<BucketisedMember> bucketisedMembers = bucketisedMemberSaver.members;

        assertThat(bucketisedMembers).hasSize(1);
        assertThat(bucketisedMembers.getFirst()).usingRecursiveComparison().isEqualTo(expectedBucketisedMember);
    }

    private static class MockedBucketisedMemberSaver implements BucketisedMemberSaver {
        private final List<BucketisedMember> members = new ArrayList<>();


        @Override
        public void addBucketisedMember(BucketisedMember bucketisedMember) {
            members.add(bucketisedMember);
        }

        @Override
        public void flush() {
            // irrelevant for this test
        }
    }
}
