package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberConverterTest {

	@Test
	void test_writeInternal_isNotSupported() {
		assertThrows(UnsupportedOperationException.class,
				() -> new MemberConverter(new AppConfig()).writeInternal(null, null));
	}

}