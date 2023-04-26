package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.InvalidModelIdException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
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
import java.util.Objects;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.fromString;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.REST_ADMIN;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelServiceImpl.extractIdFromResource;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.riot.RDFFormat.TURTLE;

public class LdesConfigModelConverter extends AbstractHttpMessageConverter<LdesConfigModel> {

	private static List<Resource> resources = List.of(createResource(EVENT_STREAM_TYPE),
			createResource(TREE_NODE_RESOURCE), createResource(NODE_SHAPE_TYPE));

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(LdesConfigModel.class);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.ALL);
	}

	@Override
	protected LdesConfigModel readInternal(Class<? extends LdesConfigModel> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		Lang lang = getLang(Objects.requireNonNull(inputMessage.getHeaders().getContentType()), REST_ADMIN);
		Model model = fromString(new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8), lang);
		String memberId = extractStreamId(model);
		return new LdesConfigModel(memberId, model);
	}

	private String extractStreamId(Model model) {
		for (Resource resource : resources) {
			Optional<Statement> statementOptional = model.listStatements(null, RDF_SYNTAX_TYPE, resource)
					.nextOptional();
			if (statementOptional.isPresent()) {
				Statement statement = statementOptional.get();
				return extractIdFromResource(statement.getSubject());
			}
		}
		throw new InvalidModelIdException(RdfModelConverter.toString(model, Lang.TURTLE));
	}

	@Override
	protected void writeInternal(LdesConfigModel ldesConfigModel, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Model model = ldesConfigModel.getModel();

		StringWriter outputStream = new StringWriter();

		RDFDataMgr.write(outputStream, model, TURTLE);

		OutputStream body = outputMessage.getBody();
		body.write(outputStream.toString().getBytes());
	}
}
