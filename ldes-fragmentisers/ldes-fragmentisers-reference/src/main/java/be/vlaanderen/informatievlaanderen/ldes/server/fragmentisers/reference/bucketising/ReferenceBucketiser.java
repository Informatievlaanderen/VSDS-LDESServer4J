package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.bucketising;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.config.ReferenceConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.Set;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

/**
 * Creates reference buckets based on the property path for the member to be added to.
 */
public class ReferenceBucketiser {
	private final String fragmentationPath;

	public ReferenceBucketiser(ReferenceConfig referenceConfig) {
		this.fragmentationPath = referenceConfig.fragmentationPath();
	}

	public Set<String> bucketise(String memberId, Model memberModel) {
		final String memberSubject = memberId.substring(memberId.indexOf("/") + 1);
		return memberModel
				.listObjectsOfProperty(createResource(memberSubject), createProperty(fragmentationPath))
				.filterKeep(RDFNode::isURIResource)
				.mapWith(RDFNode::asResource)
				.mapWith(Resource::getURI)
				.toSet();
	}
}
