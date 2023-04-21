package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
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

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.fromString;
import static org.apache.jena.riot.RDFFormat.TURTLE;

public class ModelConverter extends AbstractHttpMessageConverter<Model> {

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(Model.class);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.ALL);
	}

	@Override
	protected Model readInternal(Class<? extends Model> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return fromString(new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8),
				Lang.TURTLE);
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
