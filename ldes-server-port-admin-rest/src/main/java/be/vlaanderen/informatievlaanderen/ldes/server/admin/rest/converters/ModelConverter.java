package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.REST_ADMIN;
import static org.apache.jena.riot.RDFFormat.TURTLE;

public class ModelConverter extends AbstractHttpMessageConverter<Model> {

	@Override
	protected boolean supports(Class<?> clazz) {
		return Model.class.isAssignableFrom(clazz);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.ALL);
	}

	@Override
	protected Model readInternal(Class<? extends Model> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		Lang lang = getLang(Objects.requireNonNull(inputMessage.getHeaders().getContentType()), REST_ADMIN);
		return RdfModelConverter.fromString(new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8),
				lang);
	}

	@Override
	protected void writeInternal(Model model, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		StringWriter outputStream = new StringWriter();

		RDFDataMgr.write(outputStream, model, TURTLE);

		OutputStream body = outputMessage.getBody();
		body.write(outputStream.toString().getBytes());
	}
}
