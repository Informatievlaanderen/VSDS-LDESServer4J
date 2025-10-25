package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfMediaType;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services.TreeNodeConverter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.FETCH;

public class TreeNodeHttpConverter implements HttpMessageConverter<TreeNode> {

	private final TreeNodeConverter treeNodeConverter;
	private final RdfModelConverter rdfModelConverter;

	public TreeNodeHttpConverter(TreeNodeConverter treeNodeConverter, RdfModelConverter rdfModelConverter) {
		this.treeNodeConverter = treeNodeConverter;
		this.rdfModelConverter = rdfModelConverter;
	}

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return clazz.isAssignableFrom(TreeNode.class);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
        return RdfMediaType.getMediaTypes();
	}

	@Override
	public TreeNode read(Class<? extends TreeNode> clazz, HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		return null;
	}

	@Override
	public void write(TreeNode treeNode, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Lang lang = rdfModelConverter.getLangOrDefault(contentType, FETCH);
		Model fragmentModel = treeNodeConverter.toModel(treeNode);
		outputMessage.getHeaders().setContentType(MediaType.parseMediaType(lang.getHeaderString()));
		RDFWriter.source(fragmentModel).lang(lang).output(outputMessage.getBody());
	}
}
