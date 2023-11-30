package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.RequestContextExtracter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.FETCH;

public class ViewHttpConverter implements HttpMessageConverter<ViewSpecification> {

	private final ViewSpecificationConverter viewSpecificationConverter;
	private final RequestContextExtracter requestContextExtracter;
	private final boolean useRelativeUrl;

	public ViewHttpConverter(ViewSpecificationConverter viewSpecificationConverter, RequestContextExtracter requestContextExtracter, Boolean useRelativeUrl) {
		this.viewSpecificationConverter = viewSpecificationConverter;
		this.requestContextExtracter = requestContextExtracter;
		this.useRelativeUrl = useRelativeUrl;
	}

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return ViewSpecification.class.isAssignableFrom(clazz);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.ALL);
	}

	@Override
	public ViewSpecification read(Class<? extends ViewSpecification> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read a viewSpecification");
	}

	@Override
	public void write(ViewSpecification view, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Lang rdfFormat = getLang(contentType, FETCH);
		Model model = viewSpecificationConverter.modelFromView(view);

		if(useRelativeUrl) {
			model.write(outputMessage.getBody(), rdfFormat.getName(), requestContextExtracter.extractRequestURL());
		} else {
			model.write(outputMessage.getBody(), rdfFormat.getName());
		}
	}
}
