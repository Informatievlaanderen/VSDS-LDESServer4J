package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptions.InvalidModelException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesStreamModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
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

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.fromString;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.LdesProcessDirection.INGEST;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.config.LdesAdminConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.riot.RDFFormat.NQUADS;

public class LdesStreamModelConverter extends AbstractHttpMessageConverter<LdesStreamModel> {

	private static List<Resource> resources = List.of(createResource(EVENT_STREAM_TYPE),
			createResource(VIEW), createResource(SHAPE));

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(LdesStreamModel.class);
	}

	@Override
	protected LdesStreamModel readInternal(Class<? extends LdesStreamModel> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		Lang lang = getLang(Objects.requireNonNull(inputMessage.getHeaders().getContentType()), INGEST);
		Model memberModel = fromString(new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8), lang);
		String memberId = extractStreamId(memberModel);
		return new LdesStreamModel(memberId, memberModel);
	}

	private String extractStreamId(Model model) {
		for (Resource resource : resources) {
			Optional<Statement> statementOptional = model.listStatements(null, RDF_SYNTAX_TYPE, resource)
					.nextOptional();
			if (statementOptional.isPresent()) {
				Statement statement = statementOptional.get();
				return statement.getSubject().toString();
			}
		}
		throw new InvalidModelException(RdfModelConverter.toString(model, Lang.TURTLE));
	}

	@Override
	protected void writeInternal(LdesStreamModel ldesStreamModel, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Model fragmentModel = ldesStreamModel.getModel();

		StringWriter outputStream = new StringWriter();

		RDFDataMgr.write(outputStream, fragmentModel, NQUADS);

		OutputStream body = outputMessage.getBody();
		body.write(outputStream.toString().getBytes());
	}
}
