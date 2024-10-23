package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;

import java.util.List;

@Converter
public class ModelListConverter implements AttributeConverter<List<Model>, String> {
    @Override
    public String convertToDatabaseColumn(List<Model> attribute) {
        final Model caputeredModel = ModelFactory.createDefaultModel();
        attribute.forEach(caputeredModel::add);
        return RDFWriter.source(caputeredModel).lang(PostgresAdminConstants.SERIALISATION_LANG).asString();
    }

    @Override
    public List<Model> convertToEntityAttribute(String dbData) {
        return RDFParser.create().fromString(dbData)
                .lang(PostgresAdminConstants.SERIALISATION_LANG)
                .toModel()
                .listSubjects()
                .mapWith(subject -> subject.listProperties().toModel())
                .toList();
    }
}
