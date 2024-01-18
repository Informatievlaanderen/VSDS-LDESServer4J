package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.propertypath;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.util.List;

public interface PropertyExtractor {

	List<RDFNode> getProperties(Model model);

}
