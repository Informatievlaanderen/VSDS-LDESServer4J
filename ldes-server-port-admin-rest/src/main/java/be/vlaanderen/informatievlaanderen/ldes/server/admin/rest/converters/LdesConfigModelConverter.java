package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.InvalidModelIdException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelServiceImpl.extractIdFromResource;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

public class LdesConfigModelConverter {

	private static final List<Resource> resources = List.of(createResource(EVENT_STREAM_TYPE),
			createResource(TREE_NODE_RESOURCE), createResource(NODE_SHAPE_TYPE));

	public static LdesConfigModel toModel(String content, String contentType) {
		Model model = ModelConverter.toModel(content, contentType);
		String memberId = extractStreamId(model);
		return new LdesConfigModel(memberId, model);
	}

	public static String toString(LdesConfigModel ldesConfigModel, String contentType) {
		return ModelConverter.toString(ldesConfigModel.getModel(), contentType);
	}

	private static String extractStreamId(Model model) {
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
}
