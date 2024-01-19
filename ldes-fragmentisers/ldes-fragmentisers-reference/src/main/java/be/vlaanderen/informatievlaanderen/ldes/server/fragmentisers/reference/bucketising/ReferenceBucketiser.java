package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.bucketising;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.propertypath.PropertyPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.config.ReferenceConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Creates reference buckets based on the property path for the member to be added to.
 */
public class ReferenceBucketiser {
	private final PropertyPathExtractor propertyPathExtractor;

	public ReferenceBucketiser(ReferenceConfig referenceConfig) {
		this.propertyPathExtractor = PropertyPathExtractor.from(referenceConfig.fragmentationPath());
	}

	public Set<String> bucketise(Model memberModel) {
		return propertyPathExtractor
				.getProperties(memberModel)
				.stream()
				.map(RDFNode::toString)
				.collect(Collectors.toSet());
	}
}
