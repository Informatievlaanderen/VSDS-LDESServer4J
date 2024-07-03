package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Converter
@Component
public class DatabaseColumnModelConverter implements AttributeConverter<Model, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(Model attribute) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        RDFWriter.source(attribute).lang(PostgresIngestMemberConstants.SERIALISATION_LANG).output(stream);
        return stream.toByteArray();
    }

    @Override
    public Model convertToEntityAttribute(byte[] dbData) {
        return RDFParser.source(new ByteArrayInputStream(dbData)).lang(PostgresIngestMemberConstants.SERIALISATION_LANG).toModel();
    }
}
