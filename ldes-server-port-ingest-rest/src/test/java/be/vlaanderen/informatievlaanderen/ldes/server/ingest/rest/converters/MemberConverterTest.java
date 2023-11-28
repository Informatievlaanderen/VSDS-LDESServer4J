package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.converters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberConverterTest {

	@Test
	void test_writeInternal_isNotSupported() {
		MemberConverter memberConverter = new MemberConverter("/");
		assertThrows(UnsupportedOperationException.class,
				() -> memberConverter.writeInternal(null, null));
	}

}