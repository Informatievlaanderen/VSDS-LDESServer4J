package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.fragmentisers.FragmentiserConfigException;
import org.junit.jupiter.api.Test;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.temporalFields;
import static org.junit.jupiter.api.Assertions.*;

class TimeBasedConfigTest {

	@Test
	void when_MaxGranularityNotAllowed_Then_ThrowException() {
		Exception e = assertThrows(FragmentiserConfigException.class,
				() -> new TimeBasedConfig(".*", "", "notAllowed"));

		assertEquals("Could not create fragmentation config: notAllowed is not allowed. Allowed values are: "
				+ String.join(", ", temporalFields), e.getMessage());
	}

}
