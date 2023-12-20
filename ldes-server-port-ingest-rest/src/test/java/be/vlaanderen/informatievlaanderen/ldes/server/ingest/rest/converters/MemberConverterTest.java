package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpOutputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class MemberConverterTest {

	@Test
	void test_writeInternal_isNotSupported() {
		MemberConverter memberConverter = new MemberConverter();
		Member member = mock();
		HttpOutputMessage message = new MockHttpOutputMessage();
		assertThrows(UnsupportedOperationException.class,
				() -> memberConverter.writeInternal(member, message));
	}

}
