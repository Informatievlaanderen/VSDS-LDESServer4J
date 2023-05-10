package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.ext.com.google.common.reflect.TypeToken;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ListViewHttpConverter implements GenericHttpMessageConverter<List<ViewSpecification>> {
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return List.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
		return false;
	}

	@Override
	public List<ViewSpecification> read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read a list of viewSpecifications");
	}

	@Override
	public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
		TypeToken<List<ViewSpecification>> expectedType = new TypeToken<>() {
		};
		return canWrite(clazz, mediaType) && expectedType.isSupertypeOf(type);
	}

	@Override
	public void write(List<ViewSpecification> models, Type type, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		write(models, contentType, outputMessage);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.ALL);
	}

	@Override
	public List<ViewSpecification> read(Class<? extends List<ViewSpecification>> clazz,
			HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read a list of viewSpecifications");
	}

	@Override
	public void write(List<ViewSpecification> views, MediaType contentType,
			HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		Model model = ModelFactory.createDefaultModel();
		views.stream().map(ViewSpecificationConverter::modelFromView).forEach(model::add);

		StringWriter outputStream = new StringWriter();

		RDFDataMgr.write(outputStream, model, Lang.TURTLE);

		outputMessage.getBody().write(outputStream.toString().getBytes(StandardCharsets.UTF_8));
	}
}
