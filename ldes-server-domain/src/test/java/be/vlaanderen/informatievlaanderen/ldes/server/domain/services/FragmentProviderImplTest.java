package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FragmentProviderImplTest {
    private final FragmentCreator fragmentCreator = mock(FragmentCreator.class);

    private final LdesFragmentConfig ldesFragmentConfig = new LdesFragmentConfig();
    private final FragmentProvider fragmentProvider = new FragmentProviderImpl(fragmentCreator, ldesFragmentConfig);

    @DisplayName("Fetching of an LdesFragment")
    @Test
    void when_LdesFragmentIsFetched_CreatedResourceIsReturned() {
        LdesMember firstMember = new LdesMember("_:subject1 <http://an.example/predicate1> \"object1\" .", Lang.NQUADS);
        LdesMember secondMember = new LdesMember("_:subject2 <http://an.example/predicate2> \"object2\" .", Lang.NQUADS);

        ldesFragmentConfig.setView("http://an.example/view");

         LdesFragment expectedLdesFragment = new LdesFragment(List.of(firstMember, secondMember), ldesFragmentConfig.toMap());
         when(fragmentCreator.createFragment(ldesFragmentConfig.toMap())).thenReturn(expectedLdesFragment);

         LdesFragment actualLdesFragment = fragmentProvider.getFragment();

         assertEquals(expectedLdesFragment, actualLdesFragment);
         verify(fragmentCreator, times(1)).createFragment(ldesFragmentConfig.toMap());
    }
}