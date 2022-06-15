package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FragmentCreatorImplTest {

    private final LdesMemberRepository ldesMemberRepository = mock(LdesMemberRepository.class);
    private final FragmentCreator fragmentCreator = new FragmentCreatorImpl(ldesMemberRepository);

    private final LdesFragmentConfig ldesFragmentConfig = new LdesFragmentConfig();

    @DisplayName("Creation of a LdesFragment")
    @Test
    void when_LdesMemberIsStoredInRepository_CreatedResourceIsReturned() {

        LdesMember firstMember = new LdesMember("_:subject1 <http://an.example/predicate1> \"object1\" .", Lang.NQUADS);
        LdesMember secondMember = new LdesMember("_:subject2 <http://an.example/predicate2> \"object2\" .",
                Lang.NQUADS);
        List<LdesMember> ldesMembers = List.of(firstMember, secondMember);
        when(ldesMemberRepository.fetchLdesMembers()).thenReturn(ldesMembers);

        ldesFragmentConfig.setView("http://an.example/view");

        LdesFragment actualLdesFragment = fragmentCreator.createFragment(ldesFragmentConfig.toMap());

        assertEquals(actualLdesFragment.getMembers(), ldesMembers);
        verify(ldesMemberRepository, times(1)).fetchLdesMembers();
    }

}