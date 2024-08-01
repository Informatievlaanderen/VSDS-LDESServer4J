package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.relations.RelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelationCreatedEvent;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.springframework.context.ApplicationEventPublisher;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE;

public class ReferenceFragmentRelationsAttributer implements RelationsAttributer {

	public static final String TREE_REFERENCE_EQUALS_RELATION = TREE + "EqualToRelation";

	private final ApplicationEventPublisher applicationEventPublisher;
	private final String fragmentationPath;
	private final String fragmentKeyReference;

	public ReferenceFragmentRelationsAttributer(ApplicationEventPublisher applicationEventPublisher,
	                                            String fragmentationPath,
	                                            String fragmentKeyReference) {
		this.applicationEventPublisher = applicationEventPublisher;
		this.fragmentationPath = fragmentationPath;
		this.fragmentKeyReference = fragmentKeyReference;
	}

	public void addRelationFromRootToBottom(Bucket rootBucket, Bucket referenceBucket) {
		final BucketRelation bucketRelation = new BucketRelation(
				rootBucket,
				referenceBucket,
				TREE_REFERENCE_EQUALS_RELATION,
				getTreeValue(referenceBucket),
				XSDDatatype.XSDanyURI.getURI(),
				fragmentationPath
		);
		applicationEventPublisher.publishEvent(new BucketRelationCreatedEvent(bucketRelation));
	}


	public void addDefaultRelation(Bucket rootBucket, Bucket referenceBucket) {
		final BucketRelation defaultRelation = BucketRelation.createGenericRelation(rootBucket, referenceBucket);
		applicationEventPublisher.publishEvent(new BucketRelationCreatedEvent(defaultRelation));
	}

	private String getTreeValue(Bucket currentBucket) {
		return currentBucket
				.getValueForKey(fragmentKeyReference)
				.orElseThrow(
						() -> new MissingFragmentValueException(currentBucket.createPartialUrl(), fragmentKeyReference)
				);
	}
}
