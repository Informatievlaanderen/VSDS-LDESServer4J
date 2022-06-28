package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFParserBuilder;

import java.io.StringWriter;

public class RdfModelConverter {
    private RdfModelConverter() {

    }

    public static Model fromString(final String content, final Lang lang) {
        return RDFParserBuilder.create().fromString(content).lang(lang).toModel();
    }

    public static String toString(final Model model, final RDFFormat rdfFormat) {
        StringWriter stringWriter = new StringWriter();
        RDFDataMgr.write(stringWriter, model, rdfFormat);
        return stringWriter.toString();
    }
}
