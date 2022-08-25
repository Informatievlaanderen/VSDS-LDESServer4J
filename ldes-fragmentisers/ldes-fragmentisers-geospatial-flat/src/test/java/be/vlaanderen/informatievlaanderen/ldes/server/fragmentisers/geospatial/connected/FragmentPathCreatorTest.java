package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { GeospatialConfig.class, LdesConfig.class })
@EnableConfigurationProperties
@ActiveProfiles("fragment-generator-test")
class FragmentPathCreatorTest {

	@Autowired
	private GeospatialConfig geospatialConfig;
	@Autowired
	private LdesConfig ldesConfig;

	private FragmentCreator fragmentCreator;

	private FragmentPathCreator fragmentPathCreator;

	@BeforeEach
	void setUp() {
		fragmentCreator = new GeospatialFragmentCreator(ldesConfig, new GeospatialFragmentNamingStrategy());
		fragmentPathCreator = new FragmentPathCreator(geospatialConfig, fragmentCreator);
	}

	@Test
	@DisplayName("Verify Correct Generation of in between fragments")
	void when_StartAndEndFragmentsAreGiven_InBetweenFragmentsAreGenerated() {
		Set<String> expectedFragmentIds = getExpectedFragmentIdsInBetween();
		LdesFragment firstFragment = fragmentCreator.createNewFragment(Optional.empty(),
				new FragmentPair(GeospatialConstants.FRAGMENT_KEY_TILE, "15/0/0"));
		LdesFragment secondFragment = fragmentCreator.createNewFragment(Optional.empty(),
				new FragmentPair(GeospatialConstants.FRAGMENT_KEY_TILE, "15/3/5"));

		Set<LdesFragment> ldesFragments = fragmentPathCreator.createFragmentPath(firstFragment, secondFragment, fragmentPairList);
		Set<String> actualFragmentIds = ldesFragments
				.stream()
				.map(LdesFragment::getFragmentId)
				.collect(Collectors.toSet());

		assertEquals(expectedFragmentIds, actualFragmentIds);
	}

	private Set<String> getExpectedFragmentIdsInBetween() {
		return Stream.of("15/0/1", "15/0/2", "15/0/3", "15/0/4", "15/0/5", "15/1/5", "15/2/5")
				.map(bucket -> "http://localhost:8080/mobility-hindrances?tile=" + bucket)
				.collect(Collectors.toSet());
	}
}