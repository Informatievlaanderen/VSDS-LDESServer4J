package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import org.apache.commons.io.IOUtils;
import org.apache.jena.atlas.io.InputStreamBuffered;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.StringWriter;
import java.nio.charset.Charset;

public class LdesMemberConverterImpl implements LdesMemberConverter {
    private final JSONParser parser = new JSONParser();
    private static final String DELIMITER = "\n";

    @Override
    public LdesMemberEntity toEntity(LdesMember ldesMember) {
        Model model = ModelFactory.createDefaultModel();
        readModelFromString(String.join(DELIMITER, ldesMember.getQuads()), model, Lang.NQUADS);
        String jsonLDString = writeToString(model, Lang.JSONLD11);
        JSONObject jsonObject = parseStringToJsonObject(jsonLDString);
        return new LdesMemberEntity(jsonObject.hashCode(), jsonObject);
    }

    @Override
    public LdesMember fromEntity(LdesMemberEntity ldesMemberEntity) {
        Model model = ModelFactory.createDefaultModel();
        readModelFromString(ldesMemberEntity.getLdesMember().toJSONString(), model, Lang.JSONLD11);
        String quadString = writeToString(model, Lang.NQUADS);
        return new LdesMember(quadString.split(DELIMITER));
    }

    private JSONObject parseStringToJsonObject(String jsonLDString) {
        try {
            return (JSONObject) parser.parse(jsonLDString);
        } catch (ParseException e) {
            throw new RuntimeException(
                    String.format("Following String could not be converted to a proper json object: %s", jsonLDString),
                    e);
        }
    }

    private String writeToString(Model model, Lang lang) {
        StringWriter outputStream = new StringWriter();
        RDFDataMgr.write(outputStream, model, lang);
        return outputStream.toString();
    }

    private void readModelFromString(String inputString, Model model, Lang lang) {
        RDFDataMgr.read(model, new InputStreamBuffered(IOUtils.toInputStream(inputString, Charset.defaultCharset())),
                lang);
    }

}
