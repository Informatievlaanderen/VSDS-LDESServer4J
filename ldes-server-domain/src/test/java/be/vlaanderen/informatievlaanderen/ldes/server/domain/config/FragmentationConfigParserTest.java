package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = { FragmentationConfig.class })
@EnableConfigurationProperties
@ActiveProfiles("fragmentation-config-test")
class FragmentationConfigParserTest {

	@Autowired
	private FragmentationConfig fragmentationConfig;

	@Test
	void when_FragmentationConfigIsParsed_ListOfFramgentationSpecificationsIsReturned() {
		List<FragmentationSpecification> fragmentationSpecifications = FragmentationConfigParser
				.getFragmentationSpecifications(fragmentationConfig);
		assertEquals(2, fragmentationSpecifications.size());
		assertEquals(getGeospatialFragmentationSpecification(), fragmentationSpecifications.get(0));
		assertEquals(getTimebasedFragmentationSpecification(), fragmentationSpecifications.get(1));
	}

	private FragmentationSpecification getTimebasedFragmentationSpecification() {
		return new FragmentationSpecification("timebased", new FragmentationProperties(Map.of("memberLimit", "5")));
	}

	private FragmentationSpecification getGeospatialFragmentationSpecification() {
		return new FragmentationSpecification("geospatial",
				new FragmentationProperties(Map.of("maxZoomLevel", "15", "bucketiserProperty",
						"http://www.opengis.net/ont/geosparql#asWKT",
						"projection", "lambert72")));
	}

}