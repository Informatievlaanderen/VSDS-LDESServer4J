package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LdesConfig.class})
@EnableConfigurationProperties
@ActiveProfiles("test")
class TimeBasedFragmentNamingStrategyTest {
	
	@Autowired
	private LdesConfig ldesConfig;
	
	private FragmentInfo fragmentInfo;
	private LdesFragmentNamingStrategy ldesFragmentNamingStrategy;
	
	@BeforeEach
	void setup() {
		fragmentInfo = new FragmentInfo(ldesConfig.getHostName(), ldesConfig.getShape(), ldesConfig.getCollectionName(), List.of(new FragmentPair("timestampPath", "2020-12-05T09:00:00.000Z")));
		
		ldesFragmentNamingStrategy = new TimeBasedFragmentNamingStrategy();
	}
	
    @Test
    void when_FragmentIdConverter_ToFragmentId_ExpectCorrectFormat() {
        String fragmentId = ldesFragmentNamingStrategy.generateFragmentName(ldesConfig, fragmentInfo);

        String expectedFragmentId = "http://localhost:8080/testData?timestampPath=2020-12-05T09:00:00.000Z";

        assertEquals(expectedFragmentId, fragmentId);
    }
}
