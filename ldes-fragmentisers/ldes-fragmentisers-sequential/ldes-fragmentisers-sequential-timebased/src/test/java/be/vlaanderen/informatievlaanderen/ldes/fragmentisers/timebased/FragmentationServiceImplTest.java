package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LdesConfig.class})
@EnableConfigurationProperties
@ActiveProfiles("test")
class FragmentationServiceImplTest {

    @Autowired
    private LdesConfig ldesConfig;

    private final LdesMemberRepository ldesMemberRepository = mock(LdesMemberRepository.class);
    private final LdesFragmentRepository ldesFragmentRespository = mock(LdesFragmentRepository.class);

    private final FragmentCreator fragmentCreator = mock(FragmentCreator.class);

    private FragmentationService fragmentationService;

    @BeforeEach
    void setUp() {
        fragmentationService = new TimeBasedFragmentationService(ldesConfig, fragmentCreator, ldesMemberRepository, ldesFragmentRespository);
    }

    @Test
    @DisplayName("Member not found in repository")
    void when_MemberIsNotFoundInRepository_thenMemberNotFoundExceptionIsThrown() {
        MemberNotFoundException memberNotFoundException = assertThrows(MemberNotFoundException.class, () -> fragmentationService.addMemberToFragment("nonExistingMember"));

        assertEquals("Member with id nonExistingMember not found in repository", memberNotFoundException.getMessage());
    }

    @Test
    @DisplayName("Adding Member when there is no existing fragment")
    void when_NoFragmentExists_thenFragmentIsCreatedAndMemberIsAdded() throws IOException {
        String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"), StandardCharsets.UTF_8);
        LdesMember ldesMember = new LdesMember("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1", RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
        LdesFragment createdFragment = new LdesFragment("fragmentId", new FragmentInfo("view", "shape", "viewShortName", List.of(new FragmentPair("Path", "Value"))));
        when(ldesMemberRepository.getLdesMemberById(ldesMember.getLdesMemberId())).thenReturn(Optional.of(ldesMember));
        when(ldesFragmentRespository.retrieveOpenFragment(ldesConfig.getCollectionName()))
                .thenReturn(Optional.empty());
        when(fragmentCreator.createNewFragment(Optional.empty(), ldesMember))
                .thenReturn(createdFragment);

        fragmentationService.addMemberToFragment(ldesMember.getLdesMemberId());

        InOrder inOrder = inOrder(ldesFragmentRespository, fragmentCreator, ldesMemberRepository);
        inOrder.verify(ldesMemberRepository, times(1)).getLdesMemberById(ldesMember.getLdesMemberId());
        inOrder.verify(ldesFragmentRespository, times(1)).retrieveOpenFragment(ldesConfig.getCollectionName());
        inOrder.verify(fragmentCreator, times(1)).createNewFragment(Optional.empty(), ldesMember);
        inOrder.verify(ldesFragmentRespository, times(1)).saveFragment(createdFragment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Adding Member when there is an incomplete fragment")
    void when_AnIncompleteFragmentExists_thenMemberIsAddedToFragment() throws IOException {
        String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"), StandardCharsets.UTF_8);
        LdesMember ldesMember = new LdesMember("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1", RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
        LdesFragment existingLdesFragment = new LdesFragment("fragmentId", new FragmentInfo("view", "shape", "viewShortName", List.of(new FragmentPair("Path", "Value"))));
        when(ldesMemberRepository.getLdesMemberById(ldesMember.getLdesMemberId())).thenReturn(Optional.of(ldesMember));
        when(ldesFragmentRespository.retrieveOpenFragment(ldesConfig.getCollectionName()))
                .thenReturn(Optional.of(existingLdesFragment));

        fragmentationService.addMemberToFragment(ldesMember.getLdesMemberId());

        InOrder inOrder = inOrder(ldesFragmentRespository, fragmentCreator, ldesMemberRepository);
        inOrder.verify(ldesMemberRepository, times(1)).getLdesMemberById(ldesMember.getLdesMemberId());
        inOrder.verify(ldesFragmentRespository, times(1)).retrieveOpenFragment(ldesConfig.getCollectionName());
        inOrder.verify(fragmentCreator, never()).createNewFragment(any(), any());
        inOrder.verify(ldesFragmentRespository, times(1)).saveFragment(existingLdesFragment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Adding Member when there is a complete fragment")
    void when_AFullFragmentExists_thenANewFragmentIsCreatedAndMemberIsAddedToNewFragment() throws IOException {
        String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"), StandardCharsets.UTF_8);
        LdesMember ldesMember = new LdesMember("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1", RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
        LdesFragment existingLdesFragment = new LdesFragment("existingFragment", new FragmentInfo("view", "shape", "viewShortName", List.of(new FragmentPair("Path", "Value"))));
        LdesFragment newFragment = new LdesFragment("someId", new FragmentInfo("view", "shape", "viewShortName", List.of(new FragmentPair("Path", "Value"))));
        IntStream.range(0, 5).forEach(index -> existingLdesFragment.addMember("memberId"));
        when(ldesMemberRepository.getLdesMemberById(ldesMember.getLdesMemberId())).thenReturn(Optional.of(ldesMember));
        when(ldesFragmentRespository.retrieveOpenFragment(ldesConfig.getCollectionName()))
                .thenReturn(Optional.of(existingLdesFragment));
        when(fragmentCreator.needsToCreateNewFragment(existingLdesFragment)).thenReturn(true);
        when(fragmentCreator.createNewFragment(Optional.of(existingLdesFragment), ldesMember)).thenReturn(newFragment);

        fragmentationService.addMemberToFragment(ldesMember.getLdesMemberId());

        InOrder inOrder = inOrder(ldesFragmentRespository, fragmentCreator, ldesMemberRepository);
        inOrder.verify(ldesMemberRepository, times(1)).getLdesMemberById(ldesMember.getLdesMemberId());
        inOrder.verify(ldesFragmentRespository, times(1)).retrieveOpenFragment(ldesConfig.getCollectionName());
        inOrder.verify(fragmentCreator, times(1)).createNewFragment(Optional.of(existingLdesFragment), ldesMember);
        inOrder.verify(ldesFragmentRespository, times(1)).saveFragment(newFragment);
        inOrder.verifyNoMoreInteractions();
    }

}