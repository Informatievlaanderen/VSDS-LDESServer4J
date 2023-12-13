package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.REST_ADMIN;

public class HttpModelConverter implements HttpMessageConverter<Model> {

	private final PrefixAdder prefixAdder;
	private final RdfModelConverter rdfModelConverter;
	private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.valueOf("text/turtle");

	public HttpModelConverter(PrefixAdder prefixAdder, RdfModelConverter rdfModelConverter) {
		this.prefixAdder = prefixAdder;
		this.rdfModelConverter = rdfModelConverter;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return Model.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return Model.class.isAssignableFrom(clazz);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(DEFAULT_MEDIA_TYPE, MediaType.ALL);
	}

	@Override
	public void write(Model model, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		RDFDataMgr.write(outputMessage.getBody(), prefixAdder.addPrefixesToModel(model),
				rdfModelConverter.getLang(contentType, REST_ADMIN));
	}

	@Override
	public Model read(Class<? extends Model> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		Lang lang = rdfModelConverter.getLang(Objects.requireNonNull(inputMessage.getHeaders().getContentType()), REST_ADMIN);
		return RdfModelConverter.fromString(new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8),
				lang);
	}
}
