package be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.valueobjects.TreeNodeDto;
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
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.FETCH;

public class TreeNodeHttpConverter implements HttpMessageConverter<TreeNodeDto> {

	private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.valueOf("text/turtle");

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return clazz.isAssignableFrom(TreeNodeDto.class);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(DEFAULT_MEDIA_TYPE, MediaType.ALL);
	}

	@Override
	public TreeNodeDto read(Class<? extends TreeNodeDto> clazz, HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		return null;
	}

	@Override
	public void write(TreeNodeDto treeNodeDto, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		OutputStream body = outputMessage.getBody();
		Lang rdfFormat = getLang(contentType, FETCH);
		Model fragmentModel = treeNodeDto.getModel();
		String outputString = RdfModelConverter.toString(fragmentModel, rdfFormat);
		body.write(outputString.getBytes());
	}
}
