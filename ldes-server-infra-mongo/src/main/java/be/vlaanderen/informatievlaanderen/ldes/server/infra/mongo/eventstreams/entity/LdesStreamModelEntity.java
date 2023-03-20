package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesStreamModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.StringWriter;

@Document("ldesstreammodel")
public class LdesStreamModelEntity {
	@Id
	private final String id;

	private final String model;

	public LdesStreamModelEntity(String id, String model) {
		this.id = id;
		this.model = model;
	}

	public static LdesStreamModelEntity fromLdesStreamModel(LdesStreamModel ldesStreamModel) {
		StringWriter outputStream = new StringWriter();
		RDFDataMgr.write(outputStream, ldesStreamModel.getModel(), Lang.NQUADS);
		String ldesMemberString = outputStream.toString();
		return new LdesStreamModelEntity(
				ldesStreamModel.getId(),
				ldesMemberString);
	}

	public LdesStreamModel toLdesStreamModel() {
		Model ldesMemberModel = RDFParserBuilder.create().fromString(this.model).lang(Lang.NQUADS).toModel();
		return new LdesStreamModel(this.id, ldesMemberModel);
	}

}
