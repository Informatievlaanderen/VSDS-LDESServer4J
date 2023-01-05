package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services.TreeNodeConverter;
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

public class TreeNodeHttpConverter implements HttpMessageConverter<TreeNode> {

	private final TreeNodeConverter treeNodeConverter;

	public TreeNodeHttpConverter(TreeNodeConverter treeNodeConverter) {
		this.treeNodeConverter = treeNodeConverter;
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
		return List.of(MediaType.ALL);
	}

	@Override
	public TreeNode read(Class<? extends TreeNode> clazz, HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		return null;
	}

	@Override
	public void write(TreeNode treeNode, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		OutputStream body = outputMessage.getBody();
		Lang rdfFormat = getLang(contentType, FETCH);
		Model fragmentModel = treeNodeConverter.toModel(treeNode);
		String outputString = RdfModelConverter.toString(fragmentModel, rdfFormat);
		body.write(outputString.getBytes());
	}
}
