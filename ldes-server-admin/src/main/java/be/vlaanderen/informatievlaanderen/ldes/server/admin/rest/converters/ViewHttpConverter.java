package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import io.micrometer.observation.annotation.Observed;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.FETCH;

@Observed
@Component
public class ViewHttpConverter implements HttpMessageConverter<ViewSpecification> {
	private final ViewSpecificationConverter viewSpecificationConverter;
	private final RdfModelConverter rdfModelConverter;

	public ViewHttpConverter(ViewSpecificationConverter viewSpecificationConverter, RdfModelConverter rdfModelConverter) {
		this.viewSpecificationConverter = viewSpecificationConverter;
		this.rdfModelConverter = rdfModelConverter;
	}

	@Override
	public boolean canRead(@NotNull Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(@NotNull Class<?> clazz, MediaType mediaType) {
		return ViewSpecification.class.isAssignableFrom(clazz);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.ALL);
	}

	@Override
	public ViewSpecification read(@NotNull Class<? extends ViewSpecification> clazz, @NotNull HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read a viewSpecification");
	}

	@Override
	public void write(@NotNull ViewSpecification view, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Lang rdfFormat = rdfModelConverter.getLang(contentType, FETCH);
		rdfModelConverter.checkLangForRelativeUrl(rdfFormat);
		StringWriter outputStream = new StringWriter();
		Model model = viewSpecificationConverter.modelFromView(view);

		RDFDataMgr.write(outputStream, model, rdfFormat);

		OutputStream body = outputMessage.getBody();
		body.write(outputStream.toString().getBytes());
	}
}
