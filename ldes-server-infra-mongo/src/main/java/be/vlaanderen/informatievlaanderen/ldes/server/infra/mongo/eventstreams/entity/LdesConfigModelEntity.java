package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.StringWriter;

@Document("ldesstreammodel")
public class LdesConfigModelEntity {
	@Id
	private final String id;

	private final String model;

	public LdesConfigModelEntity(String id, String model) {
		this.id = id;
		this.model = model;
	}

	public static LdesConfigModelEntity fromLdesStreamModel(LdesConfigModel ldesConfigModel) {
		StringWriter outputStream = new StringWriter();
		RDFDataMgr.write(outputStream, ldesConfigModel.getModel(), Lang.TURTLE);
		String ldesMemberString = outputStream.toString();
		return new LdesConfigModelEntity(
				ldesConfigModel.getId(),
				ldesMemberString);
	}

	public LdesConfigModel toLdesStreamModel() {
		Model ldesMemberModel = RDFParserBuilder.create().fromString(this.model).lang(Lang.TURTLE).toModel();
		return new LdesConfigModel(this.id, ldesMemberModel);
	}

}
