package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converters.JSONConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converters.JenaConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converters.JenaConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converters.JsonConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.json.simple.JSONObject;

public class LdesMemberConverterImpl implements LdesMemberConverter {
	
	private final JenaConverter jenaConverter = new JenaConverterImpl();
    private final JSONConverter jsonConverter = new JsonConverterImpl();
    private static final String DELIMITER = "\n";

    @Override
    public LdesMemberEntity toEntity(LdesMember ldesMember) {
        Model model = ModelFactory.createDefaultModel();
        jenaConverter.readModelFromString(String.join(DELIMITER, ldesMember.getQuads()), model, Lang.NQUADS);
        String jsonLDString = jenaConverter.writeModelToString(model, RDFFormat.JSONLD_COMPACT_FLAT);
        JSONObject jsonObject = jsonConverter.convertStringToJSONObject(jsonLDString);
        return new LdesMemberEntity(jsonObject.hashCode(), jsonObject);
    }

    @Override
    public LdesMember fromEntity(LdesMemberEntity ldesMemberEntity) {
        Model model = ModelFactory.createDefaultModel();
        jenaConverter.readModelFromString(ldesMemberEntity.getLdesMember().toJSONString(), model, Lang.JSONLD11);
        String quadString = jenaConverter.writeModelToString(model, RDFFormat.NQUADS);
        return new LdesMember(quadString.split(DELIMITER));
    }
}
