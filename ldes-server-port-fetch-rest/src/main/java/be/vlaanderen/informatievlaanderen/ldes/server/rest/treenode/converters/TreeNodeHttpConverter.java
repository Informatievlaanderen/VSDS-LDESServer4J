package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.RequestContextExtracter;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services.TreeNodeConverter;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.FETCH;

public class TreeNodeHttpConverter implements HttpMessageConverter<TreeNode> {

	private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.valueOf("text/turtle");

	private final TreeNodeConverter treeNodeConverter;
	private final RequestContextExtracter requestContextExtracter;
	private final boolean useRelativeUrl;

	public TreeNodeHttpConverter(TreeNodeConverter treeNodeConverter,
								 RequestContextExtracter requestContextExtracter,
								 Boolean useRelativeUrl) {
		this.treeNodeConverter = treeNodeConverter;
		this.requestContextExtracter = requestContextExtracter;
		this.useRelativeUrl = useRelativeUrl;
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
		return List.of(DEFAULT_MEDIA_TYPE, MediaType.ALL);
	}

	@Override
	public TreeNode read(Class<? extends TreeNode> clazz, HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		return null;
	}

	@Override
	public void write(TreeNode treeNode, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Lang rdfFormat = getLang(contentType, FETCH);
		Model fragmentModel = treeNodeConverter.toModel(treeNode);

		if(useRelativeUrl) {
			fragmentModel.write(outputMessage.getBody(), rdfFormat.getName(), requestContextExtracter.extractRequestURL());
		} else {
			fragmentModel.write(outputMessage.getBody(), rdfFormat.getName());
		}
	}
}
