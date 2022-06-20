package be.vlaanderen.informatievlaanderen.ldes.server.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.StringWriter;

public class LdesFragmentConverter {

    public static String outputLdesFragment(LdesFragment ldesFragment, RDFFormat rdfFormat) {
        Model fragmentModel = ldesFragment.toRdfOutputModel();

        StringWriter outputStream = new StringWriter();

        RDFDataMgr.write(outputStream, fragmentModel, rdfFormat);

        return outputStream.toString();
    }
}
