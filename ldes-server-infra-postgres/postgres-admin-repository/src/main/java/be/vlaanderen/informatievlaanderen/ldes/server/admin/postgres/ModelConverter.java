package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;

@Converter
public class ModelConverter implements AttributeConverter<Model, String> {
    @Override
    public String convertToDatabaseColumn(Model attribute) {
        return RDFWriter.source(attribute).lang(PostgresAdminConstants.SERIALISATION_LANG).asString();
    }

    @Override
    public Model convertToEntityAttribute(String dbData) {
        return RDFParser.fromString(dbData).lang(PostgresAdminConstants.SERIALISATION_LANG).toModel();
    }
}
