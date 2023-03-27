package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.ldesconfig.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ldesconfigmodel")
public class LdesConfigModelEntity {
	@Id
	private final String id;

	private final String model;

	public LdesConfigModelEntity(String id, String model) {
		this.id = id;
		this.model = model;
	}

	public static LdesConfigModelEntity fromLdesConfigModel(LdesConfigModel ldesConfigModel) {
		String ldesMemberString = RDFWriter.source(ldesConfigModel.getModel())
				.lang(Lang.TURTLE)
				.asString();
		return new LdesConfigModelEntity(
				ldesConfigModel.getId(),
				ldesMemberString);
	}

	public LdesConfigModel toLdesConfigModel() {
		Model ldesMemberModel = RDFParserBuilder.create().fromString(this.model).lang(Lang.TURTLE).toModel();
		return new LdesConfigModel(this.id, ldesMemberModel);
	}

}
