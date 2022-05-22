package be.vlaanderen.informatievlaanderen.ldes.server.domain.converters;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;

public interface JenaConverter {
    String writeModelToString(Model model, RDFFormat rdfFormat);

    void readModelFromString(String inputString, Model model, Lang lang);
}
