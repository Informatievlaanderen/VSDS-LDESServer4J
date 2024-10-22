package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.SERIALISATION_LANG;

@Converter
@Component("PaginationMemberModelConverter")
public class MemberModelConverter implements AttributeConverter<Model, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(Model attribute) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        RDFWriter.source(attribute).lang(SERIALISATION_LANG).output(stream);
        return stream.toByteArray();
    }

    @Override
    public Model convertToEntityAttribute(byte[] dbData) {
        return RDFParser.source(new ByteArrayInputStream(dbData)).lang(SERIALISATION_LANG).toModel();
    }
}
