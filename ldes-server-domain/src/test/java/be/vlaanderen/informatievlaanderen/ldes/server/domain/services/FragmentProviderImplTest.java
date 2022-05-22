package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FragmentProviderImplTest {
    private final FragmentCreator fragmentCreator = mock(FragmentCreator.class);
    private final FragmentProvider fragmentProvider = new FragmentProviderImpl(fragmentCreator);

    @DisplayName("Fetching of an LdesFragment")
    @Test
    void when_LdesFragmentIsFetched_CreatedResourceIsReturned() {
        LdesMember firstMember = new LdesMember(new String[] { "first", "ldes", "member" });
        LdesMember secondMember = new LdesMember(new String[] { "second", "ldes", "member" });
        LdesFragment expectedLdesFragment = new LdesFragment(List.of(firstMember, secondMember));
        when(fragmentCreator.createFragment()).thenReturn(expectedLdesFragment);

        LdesFragment actualLdesFragment = fragmentProvider.getFragment();

        assertEquals(expectedLdesFragment, actualLdesFragment);
        verify(fragmentCreator, times(1)).createFragment();
    }
}