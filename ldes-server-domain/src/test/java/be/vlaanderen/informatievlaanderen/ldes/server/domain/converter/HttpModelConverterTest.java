package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpModelConverterTest {
	private final HttpModelConverter httpModelConverter = new HttpModelConverter(new PrefixAdderImpl(List.of()), new RdfModelConverter());

	@Test
	void when_ReadHttpInputMessage_then_ReturnModel() throws IOException {
		final Model expectedModel = RDFDataMgr.loadModel("example-ldes-member.nq");
		final HttpInputMessage httpInputMessage = mock(HttpInputMessage.class);
		final File file = ResourceUtils.getFile("classpath:example-ldes-member.nq");
		final InputStream inputStream = new FileInputStream(file);
		when(httpInputMessage.getBody()).thenReturn(inputStream);
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.valueOf(Lang.TURTLE.getHeaderString()));
		when(httpInputMessage.getHeaders()).thenReturn(httpHeaders);

		Model actualModel = httpModelConverter.read(Model.class, httpInputMessage);

		assertThat(actualModel).matches(expectedModel::isIsomorphicWith);
	}
}