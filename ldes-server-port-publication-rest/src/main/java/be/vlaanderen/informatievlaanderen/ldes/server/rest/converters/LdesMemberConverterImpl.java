package be.vlaanderen.informatievlaanderen.ldes.server.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converters.JSONConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converters.JsonConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converters.JenaConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converters.JenaConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LdesMemberConverterImpl implements LdesMemberConverter {
    private static final String DELIMITER = "\n";
    private final JenaConverter jenaConverter = new JenaConverterImpl();
    private final JSONConverter jsonConverter = new JsonConverterImpl();

    @Override
    public JSONArray convertLdesMemberToJSONArray(LdesMember ldesMember) {
        Model model = ModelFactory.createDefaultModel();
        jenaConverter.readModelFromString(String.join(DELIMITER, ldesMember.getQuads()), model, Lang.NQUADS);
        String jsonLDString = jenaConverter.writeModelToString(model, RDFFormat.JSONLD11);
        JSONObject jsonObject = jsonConverter.convertStringToJSONObject(jsonLDString);
        return (JSONArray) jsonObject.get("@graph");
    }
}
