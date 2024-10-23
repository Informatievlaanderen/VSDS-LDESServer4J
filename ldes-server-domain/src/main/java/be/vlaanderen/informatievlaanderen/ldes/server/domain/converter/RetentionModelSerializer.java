package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.util.List;

public class RetentionModelSerializer {
	private static final Lang dbSavedLang = Lang.NQUADS;

	public List<String> serialize(List<Model> models) {
		return models
				.stream()
				.map(retentionModel -> RdfModelConverter.toString(retentionModel, dbSavedLang))
				.toList();
	}

	public List<Model> deserialize(List<String> retentionPolicies) {
		return retentionPolicies
				.stream()
				.map(retentionModel -> RDFParser.fromString(retentionModel, dbSavedLang).toModel())
				.toList();
	}
}
