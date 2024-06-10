package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DataConversionException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.persistence.AttributeConverter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;

import java.util.List;

public class ModelListConverter implements AttributeConverter<List<Model>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final CollectionType collectionType = objectMapper.getTypeFactory()
            .constructCollectionType(List.class, String.class);

    @Override
    public String convertToDatabaseColumn(List<Model> attribute) {
        final List<String> modelsAsString = attribute.stream()
                .map(model -> RDFWriter.source(model).lang(PostgresAdminConstants.SERIALISATION_LANG).asString())
                .toList();

        try {
            return objectMapper.writeValueAsString(modelsAsString);
        } catch (JsonProcessingException e) {
            throw DataConversionException.serializationFailed(List.class, e);
        }
    }

    @Override
    public List<Model> convertToEntityAttribute(String dbData) {
        try {
            final List<String> modelsAsString = objectMapper.readValue(dbData, collectionType);
            return modelsAsString.stream()
                    .map(modelString -> RDFParser.fromString(modelString).lang(PostgresAdminConstants.SERIALISATION_LANG).toModel())
                    .toList();
        } catch (JsonProcessingException e) {
            throw DataConversionException.deserializationFailed(List.class, e);
        }
    }
}
