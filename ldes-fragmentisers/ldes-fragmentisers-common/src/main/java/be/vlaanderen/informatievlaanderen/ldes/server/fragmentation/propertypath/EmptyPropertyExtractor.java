package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.propertypath;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.util.ArrayList;
import java.util.List;

public class EmptyPropertyExtractor implements PropertyExtractor {

	@Override
	public List<RDFNode> getProperties(Model model) {
		return new ArrayList<>();
	}

}
