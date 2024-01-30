package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.collection.VersionOfPathCollection;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpOutputMessage;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class MemberConverterTest {

	@Test
	void test_writeInternal_isNotSupported() {
		MemberConverter memberConverter = new MemberConverter(new RdfModelConverter(), new VersionOfPathCollection());
		Member member = mock();
		HttpOutputMessage message = new MockHttpOutputMessage();
		assertThatThrownBy(() -> memberConverter.write(member, MediaType.ALL, message))
				.isInstanceOf(UnsupportedOperationException.class);
	}

}
