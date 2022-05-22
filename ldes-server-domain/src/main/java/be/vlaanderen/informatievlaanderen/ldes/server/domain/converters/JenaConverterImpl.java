package be.vlaanderen.informatievlaanderen.ldes.server.domain.converters;

import org.apache.commons.io.IOUtils;
import org.apache.jena.atlas.io.InputStreamBuffered;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.StringWriter;
import java.nio.charset.Charset;

public class JenaConverterImpl implements JenaConverter {

    public String writeModelToString(Model model, RDFFormat rdfFormat) {
        StringWriter outputStream = new StringWriter();
        RDFDataMgr.write(outputStream, model, rdfFormat);
        return outputStream.toString();
    }

    public void readModelFromString(String inputString, Model model, Lang lang) {
        RDFDataMgr.read(model, new InputStreamBuffered(IOUtils.toInputStream(inputString, Charset.defaultCharset())),
                lang);
    }
}
