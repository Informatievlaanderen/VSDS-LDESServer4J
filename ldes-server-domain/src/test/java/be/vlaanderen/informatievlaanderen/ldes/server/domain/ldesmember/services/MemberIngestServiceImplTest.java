package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
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
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LdesConfig.class, ViewConfig.class})
@EnableConfigurationProperties
@ActiveProfiles("test")
class MemberIngestServiceImplTest {

    @Autowired
    private LdesConfig ldesConfig;
    @Autowired
    private ViewConfig viewConfig;

    private final LdesMemberRepository ldesMemberRepository = mock(LdesMemberRepository.class);
    private final LdesFragmentRespository ldesFragmentRespository = mock(LdesFragmentRespository.class);

    private final FragmentCreator fragmentCreator = mock(FragmentCreator.class);
    private MemberIngestService memberIngestService;

    @BeforeEach
    void setUp() {
        memberIngestService = new MemberIngestServiceImpl(ldesConfig, viewConfig, ldesMemberRepository, ldesFragmentRespository, fragmentCreator);
    }


    @Test
    @DisplayName("Adding Member when there is no existing fragment")
    void when_NoFragmentExists_thenFragmentIsCreatedAndMemberIsAdded() throws IOException {
        String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"), StandardCharsets.UTF_8);
        LdesMember ldesMember = new LdesMember(RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
        LdesMember expectedSavedMember = new LdesMember(RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
        LdesFragment createdFragment = new LdesFragment("someId", new FragmentInfo("view", "shape", "viewShortName", "Path", "Value"));
        when(ldesFragmentRespository.retrieveOpenFragment(ldesConfig.getCollectionName()))
                .thenReturn(Optional.empty());
        when(fragmentCreator.createNewFragment(Optional.empty(), ldesMember))
                .thenReturn(createdFragment);
        when(ldesMemberRepository.saveLdesMember(ldesMember)).thenReturn(expectedSavedMember);

        LdesMember actualLdesMember = memberIngestService.addMember(ldesMember);

        assertEquals(expectedSavedMember, actualLdesMember);
        InOrder inOrder = inOrder(ldesFragmentRespository, fragmentCreator, ldesMemberRepository);
        inOrder.verify(ldesFragmentRespository, times(1)).retrieveOpenFragment(ldesConfig.getCollectionName());
        inOrder.verify(fragmentCreator, times(1)).createNewFragment(Optional.empty(), ldesMember);
        inOrder.verify(ldesFragmentRespository, times(1)).saveFragment(createdFragment);
        inOrder.verify(ldesMemberRepository, times(1)).saveLdesMember(ldesMember);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Adding Member when there is an incomplete fragment")
    void when_AnIncompleteFragmentExists_thenMemberIsAddedToFragment() throws IOException {
        String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"), StandardCharsets.UTF_8);
        LdesMember ldesMember = new LdesMember(RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
        LdesMember expectedSavedMember = new LdesMember(RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
        LdesFragment existingLdesFragment = new LdesFragment("someId", new FragmentInfo("view", "shape", "viewShortName", "Path", "Value"));
        when(ldesFragmentRespository.retrieveOpenFragment(ldesConfig.getCollectionName()))
                .thenReturn(Optional.of(existingLdesFragment));
        when(ldesMemberRepository.saveLdesMember(ldesMember)).thenReturn(expectedSavedMember);

        LdesMember actualLdesMember = memberIngestService.addMember(ldesMember);

        assertEquals(expectedSavedMember, actualLdesMember);
        InOrder inOrder = inOrder(ldesFragmentRespository, fragmentCreator, ldesMemberRepository);
        inOrder.verify(ldesFragmentRespository, times(1)).retrieveOpenFragment(ldesConfig.getCollectionName());
        inOrder.verify(fragmentCreator, never()).createNewFragment(any(), any());
        inOrder.verify(ldesFragmentRespository, times(1)).saveFragment(existingLdesFragment);
        inOrder.verify(ldesMemberRepository, times(1)).saveLdesMember(ldesMember);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Adding Member when there is a complete fragment")
    void when_AFullFragmentExists_thenANewFragmentIsCreatedAndMemberIsAddedToNewFragment() throws IOException {
        String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"), StandardCharsets.UTF_8);
        LdesMember ldesMember = new LdesMember(RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
        LdesMember expectedSavedMember = new LdesMember(RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
        LdesFragment existingLdesFragment = new LdesFragment("existingFragment", new FragmentInfo("view", "shape", "viewShortName", "Path", "Value"));
        LdesFragment newFragment = new LdesFragment("someId", new FragmentInfo("view", "shape", "viewShortName", "Path", "Value"));
        IntStream.range(0, 5).forEach(index -> existingLdesFragment.addMember("memberId"));
        when(ldesFragmentRespository.retrieveOpenFragment(ldesConfig.getCollectionName()))
                .thenReturn(Optional.of(existingLdesFragment));
        when(fragmentCreator.createNewFragment(Optional.of(existingLdesFragment), ldesMember)).thenReturn(newFragment);
        when(ldesMemberRepository.saveLdesMember(ldesMember)).thenReturn(expectedSavedMember);

        LdesMember actualLdesMember = memberIngestService.addMember(ldesMember);

        assertEquals(expectedSavedMember, actualLdesMember);
        InOrder inOrder = inOrder(ldesFragmentRespository, fragmentCreator, ldesMemberRepository);
        inOrder.verify(ldesFragmentRespository, times(1)).retrieveOpenFragment(ldesConfig.getCollectionName());
        inOrder.verify(fragmentCreator, times(1)).createNewFragment(Optional.of(existingLdesFragment), ldesMember);
        inOrder.verify(ldesFragmentRespository, times(1)).saveFragment(newFragment);
        inOrder.verify(ldesMemberRepository, times(1)).saveLdesMember(ldesMember);
        inOrder.verifyNoMoreInteractions();
    }

}