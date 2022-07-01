package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.ModelFactory;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LdesConfig.class, ViewConfig.class})
@EnableConfigurationProperties
@ActiveProfiles("test")
class FragmentationServiceImplTest {
    private static final String VIEW_SHORTNAME = "exampleData";
    private static final String VIEW = "http://localhost:8089/exampleData";
    private static final String SHAPE = "http://localhost:8089/exampleData/shape";
    private static final String PATH = "http://www.w3.org/ns/prov#generatedAtTime";
    private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
    private static final String FRAGMENT_ID_1 = VIEW + "?generatedAtTime=" + FRAGMENTATION_VALUE_1;
    private static final FragmentInfo FRAGMENT_INFO = new FragmentInfo(VIEW, SHAPE, VIEW_SHORTNAME, PATH,
            FRAGMENTATION_VALUE_1);

    @Autowired
    private LdesConfig ldesConfig;
    @Autowired
    private ViewConfig viewConfig;

    private final LdesMemberRepository ldesMemberRepository = mock(LdesMemberRepository.class);
    private final LdesFragmentRespository ldesFragmentRespository = mock(LdesFragmentRespository.class);

    private final FragmentCreator fragmentCreator = mock(FragmentCreator.class);
    private FragmentationService fragmentationService;

    @BeforeEach
    void setUp() {
        fragmentationService = new FragmentationServiceImpl(ldesConfig, viewConfig, ldesMemberRepository, ldesFragmentRespository, fragmentCreator);
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

        LdesMember actualLdesMember = fragmentationService.addMember(ldesMember);

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

        LdesMember actualLdesMember = fragmentationService.addMember(ldesMember);

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
        IntStream.range(0, 5).forEach(index -> existingLdesFragment.addMember(new LdesMember(ModelFactory.createDefaultModel())));
        when(ldesFragmentRespository.retrieveOpenFragment(ldesConfig.getCollectionName()))
                .thenReturn(Optional.of(existingLdesFragment));
        when(fragmentCreator.createNewFragment(Optional.of(existingLdesFragment), ldesMember)).thenReturn(newFragment);
        when(ldesMemberRepository.saveLdesMember(ldesMember)).thenReturn(expectedSavedMember);

        LdesMember actualLdesMember = fragmentationService.addMember(ldesMember);

        assertEquals(expectedSavedMember, actualLdesMember);
        InOrder inOrder = inOrder(ldesFragmentRespository, fragmentCreator, ldesMemberRepository);
        inOrder.verify(ldesFragmentRespository, times(1)).retrieveOpenFragment(ldesConfig.getCollectionName());
        inOrder.verify(fragmentCreator, times(1)).createNewFragment(Optional.of(existingLdesFragment), ldesMember);
        inOrder.verify(ldesFragmentRespository, times(1)).saveFragment(newFragment);
        inOrder.verify(ldesMemberRepository, times(1)).saveLdesMember(ldesMember);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void when_getFragment_WhenNoFragmentExists_ThenReturnEmptyFragment() {
        when(ldesFragmentRespository.retrieveFragment(VIEW_SHORTNAME, PATH, FRAGMENTATION_VALUE_1)).thenReturn(Optional.empty());

        LdesFragment returnedFragment = fragmentationService.getFragment(VIEW_SHORTNAME, PATH, FRAGMENTATION_VALUE_1);

        assertEquals(0, returnedFragment.getMembers().size());
        assertNull(returnedFragment.getFragmentInfo().getValue());
        assertEquals(PATH, returnedFragment.getFragmentInfo().getPath());
        assertEquals(VIEW, returnedFragment.getFragmentInfo().getView());
    }

    @Test
    void when_getFragment_WhenExactFragmentExists_ThenReturnThatFragment() {
        LdesFragment ldesFragment = new LdesFragment(FRAGMENT_ID_1, FRAGMENT_INFO);
        LdesMember firstMember = new LdesMember(RdfModelConverter.fromString("_:subject1 <http://an.example/predicate1> \"object1\" .", Lang.NQUADS));
        ldesFragment.addMember(firstMember);

        when(ldesFragmentRespository.retrieveFragment(VIEW_SHORTNAME, PATH, FRAGMENTATION_VALUE_1))
                .thenReturn(Optional.of(ldesFragment));

        LdesFragment returnedFragment = fragmentationService.getFragment(VIEW_SHORTNAME, PATH, FRAGMENTATION_VALUE_1);

        assertEquals(1, returnedFragment.getMembers().size());
        assertEquals(FRAGMENTATION_VALUE_1, returnedFragment.getFragmentInfo().getValue());
        assertEquals(PATH, returnedFragment.getFragmentInfo().getPath());
        assertEquals(VIEW, returnedFragment.getFragmentInfo().getView());
    }

    @Test
    void when_getFragment_WhenNearbyFragmentExists_ThenReturnThatFragment() {
        LdesFragment ldesFragment = new LdesFragment(FRAGMENT_ID_1, FRAGMENT_INFO);
        LdesMember firstMember = new LdesMember(RdfModelConverter.fromString("_:subject1 <http://an.example/predicate1> \"object1\" .", Lang.NQUADS));
        ldesFragment.addMember(firstMember);

        when(ldesFragmentRespository.retrieveFragment(VIEW_SHORTNAME, PATH, "2020-12-30T00:00:00.00Z"))
                .thenReturn(Optional.of(ldesFragment));

        LdesFragment returnedFragment = fragmentationService.getFragment(VIEW_SHORTNAME, PATH, "2020-12-30T00:00:00.00Z");

        assertEquals(1, returnedFragment.getMembers().size());
        assertEquals(FRAGMENTATION_VALUE_1, returnedFragment.getFragmentInfo().getValue());
        assertEquals(PATH, returnedFragment.getFragmentInfo().getPath());
        assertEquals(VIEW, returnedFragment.getFragmentInfo().getView());
    }
}
