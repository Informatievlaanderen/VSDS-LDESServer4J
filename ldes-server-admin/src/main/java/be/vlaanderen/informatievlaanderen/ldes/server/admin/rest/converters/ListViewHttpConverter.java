package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import io.micrometer.observation.annotation.Observed;
import org.apache.jena.ext.com.google.common.reflect.TypeToken;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.FETCH;

@Observed
@Component
public class ListViewHttpConverter implements GenericHttpMessageConverter<List<ViewSpecification>> {
	private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.valueOf("text/turtle");
	private final ViewSpecificationConverter viewSpecificationConverter;

	public ListViewHttpConverter(ViewSpecificationConverter viewSpecificationConverter) {
		this.viewSpecificationConverter = viewSpecificationConverter;
	}

	@Override
	public boolean canRead(@NotNull Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(@NotNull Class<?> clazz, MediaType mediaType) {
		return List.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean canRead(@NotNull Type type, Class<?> contextClass, MediaType mediaType) {
		return false;
	}

	@Override
	public List<ViewSpecification> read(@NotNull Type type, Class<?> contextClass, @NotNull HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read a list of viewSpecifications");
	}

	@Override
	public boolean canWrite(Type type, @NotNull Class<?> clazz, MediaType mediaType) {
		TypeToken<List<ViewSpecification>> expectedType = new TypeToken<>() {
		};
		return canWrite(clazz, mediaType) && expectedType.isSupertypeOf(type);
	}

	@Override
	public void write(@NotNull List<ViewSpecification> models, Type type, MediaType contentType, @NotNull HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		write(models, contentType, outputMessage);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(DEFAULT_MEDIA_TYPE, MediaType.ALL);
	}

	@Override
	public List<ViewSpecification> read(@NotNull Class<? extends List<ViewSpecification>> clazz,
										@NotNull HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read a list of viewSpecifications");
	}

	@Override
	public void write(List<ViewSpecification> views, MediaType contentType,
					  HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		Lang rdfFormat = getLang(contentType, FETCH);
		Model model = ModelFactory.createDefaultModel();
		views.stream().map(viewSpecificationConverter::modelFromView).forEach(model::add);

		RDFDataMgr.write(outputMessage.getBody(), model, rdfFormat);
	}
}
