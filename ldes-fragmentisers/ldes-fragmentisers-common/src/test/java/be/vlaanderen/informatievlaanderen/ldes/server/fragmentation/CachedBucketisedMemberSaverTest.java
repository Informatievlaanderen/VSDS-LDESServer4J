package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class CachedBucketisedMemberSaverTest {
    private static final String MEMBER_ID = "member-id";
    private static final ViewName VIEW_NAME = new ViewName("col", "view");
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private BucketisedMemberRepository bucketisedMemberRepository;

    private CachedBucketisedMemberSaver saver;

    @BeforeEach
    void setUp() {
        saver = new CachedBucketisedMemberSaver(bucketisedMemberRepository, applicationEventPublisher);
    }

    @Test
    void test_cachingAndSaving() {
        final BucketisedMember bucketisedMember = new BucketisedMember(MEMBER_ID, VIEW_NAME, "id", 1L);
        saver.addBucketisedMember(bucketisedMember);
        saver.flush();

        verify(bucketisedMemberRepository).insertAll(List.of(bucketisedMember));
        verify(applicationEventPublisher).publishEvent(new MemberBucketisedEvent(VIEW_NAME));
    }

    @Test
    void given_noMembersAdded_test_cachingAndSaving() {
        saver.flush();

        verifyNoInteractions(bucketisedMemberRepository, applicationEventPublisher);
    }
}