package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.LdesConfigTestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LdesConfig.class, ViewConfig.class})
@EnableConfigurationProperties
@ActiveProfiles("test")
class FragmentationServiceImplTest {
    @Autowired
    private LdesConfig ldesConfig;
    @Autowired
    private ViewConfig viewConfig;

    private final LdesMemberRepository ldesMemberRepository = mock(LdesMemberRepository.class);
    private final LdesFragmentRespository ldesFragmentRespository = mock(LdesFragmentRespository.class);
    private FragmentationService fragmentationService;

    @BeforeEach
    void setUp() {
        fragmentationService = new FragmentationServiceImpl(ldesConfig, viewConfig, ldesMemberRepository, ldesFragmentRespository);
    }

    @Test
    void when_addMember_WithOneFragmentDefined_thenReturnFragment() throws IOException {
        String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"), StandardCharsets.UTF_8);
        LdesMember ldesMember = new LdesMember(ldesMemberString, Lang.NQUADS);

        LdesMember expectedProcessedMember = new LdesMember(ldesMemberString, Lang.NQUADS);
        expectedProcessedMember.resetLdesMemberView(ldesConfig);

        when(ldesFragmentRespository.retrieveFragment(ldesConfig.getCollectionName(), viewConfig.getTimestampPath(), ldesMember.getFragmentationValue(viewConfig.getTimestampPath())))
                .thenReturn(Optional.empty());
        when(ldesMemberRepository.saveLdesMember(any())).thenReturn(expectedProcessedMember);

        fragmentationService.addMember(ldesMember);

        verify(ldesFragmentRespository, times(1)).retrieveLastFragment(ldesConfig.getCollectionName());
        verify(ldesFragmentRespository, times(1)).saveFragment(any());
        verify(ldesMemberRepository, times(1)).saveLdesMember(any());
    }

    @Test
    void when_getFragment_WhenNoFragmentExists_ThenReturnEmptyFragment() {
        when(ldesFragmentRespository.retrieveFragment(VIEW_SHORTNAME, PATH, FRAGMENTATION_VALUE_1)).thenReturn(Optional.empty());

        LdesFragment returnedFragment = fragmentationService.getFragment(VIEW_SHORTNAME, PATH, FRAGMENTATION_VALUE_1);

        assertEquals(0, returnedFragment.getMembers().size());
        assertEquals(FRAGMENTATION_VALUE_1, returnedFragment.getFragmentInfo().getValue());
        assertEquals(PATH, returnedFragment.getFragmentInfo().getPath());
        assertEquals(VIEW, returnedFragment.getFragmentInfo().getView());
    }

    @Test
    void when_getFragment_WhenExactFragmentExists_ThenReturnThatFragment() {
        LdesFragment ldesFragment = new LdesFragment(FRAGMENT_ID_1, FRAGMENT_INFO);
        LdesMember firstMember = new LdesMember("_:subject1 <http://an.example/predicate1> \"object1\" .", Lang.NQUADS);
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
        LdesMember firstMember = new LdesMember("_:subject1 <http://an.example/predicate1> \"object1\" .", Lang.NQUADS);
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
