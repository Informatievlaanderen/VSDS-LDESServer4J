package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.bucketising;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.config.ReferenceConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

/**
 * Creates reference buckets based on the property path for the member to be added to.
 */
public class ReferenceBucketiser {
	private final String fragmentationPath;
	private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceBucketiser.class);

	public ReferenceBucketiser(ReferenceConfig referenceConfig) {
		this.fragmentationPath = referenceConfig.fragmentationPath();
	}

	public Set<String> bucketise(@NotNull String subject,
								 @NotNull Model memberModel) {
		try {
			final String memberSubject = subject.substring(subject.indexOf("/") + 1);
			Set<String> references = memberModel
					.listObjectsOfProperty(createResource(memberSubject), createProperty(fragmentationPath))
					.filterKeep(RDFNode::isURIResource)
					.mapWith(RDFNode::asResource)
					.mapWith(Resource::getURI)
					.toSet();
			if (references.isEmpty()){
				references.add(DEFAULT_BUCKET_STRING);
			}
			return references;
		} catch (Exception exception) {
			LOGGER.warn("Could not fragment member {} Reason: {}", subject, exception.getMessage());
			return Set.of(DEFAULT_BUCKET_STRING);
		}
	}
}
