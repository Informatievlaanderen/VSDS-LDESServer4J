package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.LdesFragmentConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.LdesProcessDirection.FETCH;

public class LdesFragmentHttpConverter implements HttpMessageConverter<LdesFragment> {

	private final LdesFragmentConverter ldesFragmentConverter;

	public LdesFragmentHttpConverter(LdesFragmentConverter ldesFragmentConverter) {
		this.ldesFragmentConverter = ldesFragmentConverter;
	}

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return clazz.isAssignableFrom(LdesFragment.class);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.valueOf("text/turtle"), MediaType.valueOf("application/ld+json"),
				MediaType.valueOf("application/n-quads"));
	}

	@Override
	public LdesFragment read(Class<? extends LdesFragment> clazz, HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		return null;
	}

	@Override
	public void write(LdesFragment ldesFragment, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		OutputStream body = outputMessage.getBody();
		Lang rdfFormat = getLang(contentType, FETCH);
		Model fragmentModel = ldesFragmentConverter.toModel(ldesFragment);
		String outputString = RdfModelConverter.toString(fragmentModel, rdfFormat);
		body.write(outputString.getBytes());
	}
}
