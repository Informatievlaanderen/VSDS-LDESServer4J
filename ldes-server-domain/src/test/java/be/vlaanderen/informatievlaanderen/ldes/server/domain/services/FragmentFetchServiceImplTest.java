package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentFetchService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentFetchServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LdesConfig.class})
@EnableConfigurationProperties
@ActiveProfiles("test")
class FragmentFetchServiceImplTest {
    private static final String VIEW_SHORTNAME = "exampleData";
    private static final String VIEW = "http://localhost:8089/exampleData";
    private static final String SHAPE = "http://localhost:8089/exampleData/shape";
    private static final String PATH = "http://www.w3.org/ns/prov#generatedAtTime";
    private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
    private static final String FRAGMENT_ID_1 = VIEW + "?generatedAtTime=" + FRAGMENTATION_VALUE_1;
    private static final FragmentInfo FRAGMENT_INFO = new FragmentInfo(VIEW, SHAPE, VIEW_SHORTNAME, List.of(new FragmentPair(PATH,
            FRAGMENTATION_VALUE_1)));

    @Autowired
    private LdesConfig ldesConfig;

    private final LdesFragmentRespository ldesFragmentRespository = mock(LdesFragmentRespository.class);

    private FragmentFetchService fragmentFetchService;

    @BeforeEach
    void setUp() {
        fragmentFetchService = new FragmentFetchServiceImpl(ldesConfig, ldesFragmentRespository);
    }

    @Test
    void when_retrieveInitialFragment_WhenNoFragmentExists_ThenReturnEmptyFragment() {
        when(ldesFragmentRespository.retrieveInitialFragment(VIEW_SHORTNAME)).thenReturn(Optional.empty());
        LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_SHORTNAME, List.of());
        LdesFragment returnedFragment = fragmentFetchService.getInitialFragment(ldesFragmentRequest);

        assertEquals(0, returnedFragment.getMemberIds().size());
        assertNull(returnedFragment.getFragmentInfo().getValue());
        assertNull(returnedFragment.getFragmentInfo().getPath());
        assertEquals(VIEW, returnedFragment.getFragmentInfo().getView());
    }

    @Test
    void when_retrieveInitialFragment_WhenExactFragmentExists_ThenReturnThatFragment() {
        LdesFragment ldesFragment = new LdesFragment(FRAGMENT_ID_1, FRAGMENT_INFO);
        ldesFragment.addMember("firstMember");

        when(ldesFragmentRespository.retrieveInitialFragment(VIEW_SHORTNAME))
                .thenReturn(Optional.of(ldesFragment));
        LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_SHORTNAME, List.of());

        LdesFragment returnedFragment = fragmentFetchService.getInitialFragment(ldesFragmentRequest);

        assertEquals(1, returnedFragment.getMemberIds().size());
        assertEquals(FRAGMENTATION_VALUE_1, returnedFragment.getFragmentInfo().getValue());
        assertEquals(PATH, returnedFragment.getFragmentInfo().getPath());
        assertEquals(VIEW, returnedFragment.getFragmentInfo().getView());
    }

    @Test
    void when_getFragment_WhenNoFragmentExists_ThenReturnEmptyFragment() {
        LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_SHORTNAME, List.of(new FragmentPair(PATH, FRAGMENTATION_VALUE_1)));
        when(ldesFragmentRespository.retrieveFragment(ldesFragmentRequest)).thenReturn(Optional.empty());

        LdesFragment returnedFragment = fragmentFetchService.getFragment(ldesFragmentRequest);

        assertEquals(0, returnedFragment.getMemberIds().size());
        assertEquals(FRAGMENTATION_VALUE_1, returnedFragment.getFragmentInfo().getValue());
        assertEquals(PATH, returnedFragment.getFragmentInfo().getPath());
        assertEquals(VIEW, returnedFragment.getFragmentInfo().getView());
    }

    @Test
    void when_getFragment_WhenExactFragmentExists_ThenReturnThatFragment() {
        LdesFragment ldesFragment = new LdesFragment(FRAGMENT_ID_1, FRAGMENT_INFO);
        ldesFragment.addMember("firstMember");
        LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_SHORTNAME, List.of(new FragmentPair(PATH, FRAGMENTATION_VALUE_1)));
        when(ldesFragmentRespository.retrieveFragment(ldesFragmentRequest))
                .thenReturn(Optional.of(ldesFragment));

        LdesFragment returnedFragment = fragmentFetchService.getFragment(ldesFragmentRequest);

        assertEquals(1, returnedFragment.getMemberIds().size());
        assertEquals(FRAGMENTATION_VALUE_1, returnedFragment.getFragmentInfo().getValue());
        assertEquals(PATH, returnedFragment.getFragmentInfo().getPath());
        assertEquals(VIEW, returnedFragment.getFragmentInfo().getView());
    }
}
