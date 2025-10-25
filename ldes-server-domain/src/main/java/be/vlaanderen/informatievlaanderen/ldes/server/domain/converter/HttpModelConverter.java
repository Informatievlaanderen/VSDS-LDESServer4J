package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParser;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
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
        return RdfMediaType.getMediaTypes();
	}

	@Override
	public void write(Model model, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Lang lang = rdfModelConverter.getLangOrDefault(contentType, REST_ADMIN);
		rdfModelConverter.checkLangForRelativeUrl(lang);
		outputMessage.getHeaders().setContentType(MediaType.parseMediaType(lang.getHeaderString()));
		RDFDataMgr.write(outputMessage.getBody(), prefixAdder.addPrefixesToModel(model), lang);
	}

	@Override
	public Model read(Class<? extends Model> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		Lang lang = rdfModelConverter.getLangOrDefault(Objects.requireNonNull(inputMessage.getHeaders().getContentType()), REST_ADMIN);
		return RDFParser.source(inputMessage.getBody()).context(rdfModelConverter.getContext()).lang(lang).toModel();
	}
}
