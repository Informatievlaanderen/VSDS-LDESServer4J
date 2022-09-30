package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FragmentFetchServiceImplTest {

	private static final String COLLECTION_NAME = "exampleData";

	private static final String VIEW_NAME = "view";

	private static final String HOSTNAME = "http://localhost:8089/";
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private static final String FRAGMENT_ID_1 = HOSTNAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME + "?generatedAtTime="
			+
			FRAGMENTATION_VALUE_1;
	private static final FragmentInfo FRAGMENT_INFO = new FragmentInfo(
			VIEW_NAME, List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));

	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);

	private FragmentFetchService fragmentFetchService;

	@BeforeEach
	void setUp() {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setHostName("http://localhost:8089");
		fragmentFetchService = new FragmentFetchServiceImpl(ldesConfig,
				ldesFragmentRepository);
	}

	@Test
	void when_getFragment_WhenNoFragmentExists_ThenMissingFragmentExceptionIsThrown() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		when(ldesFragmentRepository.retrieveFragment(ldesFragmentRequest)).thenReturn(Optional.empty());

		MissingFragmentException missingFragmentException = assertThrows(MissingFragmentException.class,
				() -> fragmentFetchService.getFragment(ldesFragmentRequest));

		assertEquals(
				"No fragment exists with fragment identifier: http://localhost:8089/view?generatedAtTime=2020-12-28T09:36:09.72Z",
				missingFragmentException.getMessage());
	}

	@Test
	void when_getFragment_WhenExactFragmentExists_ThenReturnThatFragment() {
		LdesFragment ldesFragment = new LdesFragment(FRAGMENT_INFO);
		ldesFragment.addMember("firstMember");
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		when(ldesFragmentRepository.retrieveFragment(ldesFragmentRequest)).thenReturn(Optional.of(ldesFragment));

		LdesFragment returnedFragment = fragmentFetchService.getFragment(ldesFragmentRequest);

		assertEquals(1, returnedFragment.getMemberIds().size());
		assertTrue(returnedFragment.getFragmentInfo().getValueOfKey(GENERATED_AT_TIME).isPresent());
		assertEquals(FRAGMENTATION_VALUE_1,
				returnedFragment.getFragmentInfo().getValueOfKey(GENERATED_AT_TIME).get());
	}
}
