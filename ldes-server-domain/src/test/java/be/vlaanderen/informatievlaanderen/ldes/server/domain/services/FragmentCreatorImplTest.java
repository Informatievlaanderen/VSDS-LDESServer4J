package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FragmentCreatorImplTest {

    private final LdesMemberRepository ldesMemberRepository = mock(LdesMemberRepository.class);
    private final FragmentCreator fragmentCreator = new FragmentCreatorImpl(ldesMemberRepository);

    @DisplayName("Creation of a LdesFragment")
    @Test
    void when_LdesMemberIsStoredInRepository_CreatedResourceIsReturned() {
        // LdesMember firstMember = new LdesMember(new String[] { "first", "ldes", "member" });
        // LdesMember secondMember = new LdesMember(new String[] { "second", "ldes", "member" });
        // List<LdesMember> ldesMembers = List.of(firstMember, secondMember);
        // when(ldesMemberRepository.fetchLdesMembers()).thenReturn(ldesMembers);
        //
        // LdesFragment actualLdesFragment = fragmentCreator.createFragment();
        //
        // assertEquals(actualLdesFragment.getMembers(), ldesMembers);
        // verify(ldesMemberRepository, times(1)).fetchLdesMembers();
    }

}