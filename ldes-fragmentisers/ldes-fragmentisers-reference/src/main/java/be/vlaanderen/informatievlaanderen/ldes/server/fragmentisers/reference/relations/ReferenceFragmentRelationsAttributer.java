package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.relations.RelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE;

public class ReferenceFragmentRelationsAttributer implements RelationsAttributer {

	public static final String TREE_REFERENCE_EQUALS_RELATION = TREE + "EqualToRelation";

	private final String fragmentationPath;
	private final String fragmentKeyReference;

	public ReferenceFragmentRelationsAttributer(String fragmentationPath, String fragmentKeyReference) {
		this.fragmentationPath = fragmentationPath;
		this.fragmentKeyReference = fragmentKeyReference;
	}

	public void addRelationFromRootToBottom(Bucket rootBucket, Bucket referenceBucket) {
		final BucketRelation bucketRelation = new BucketRelation(
				TREE_REFERENCE_EQUALS_RELATION,
				getTreeValue(referenceBucket),
				XSDDatatype.XSDanyURI.getURI(),
				fragmentationPath
		);
		rootBucket.addChildBucket(referenceBucket.withRelation(bucketRelation));
	}


	public void addDefaultRelation(Bucket rootBucket, Bucket referenceBucket) {
		rootBucket.addChildBucket(referenceBucket.withGenericRelation());
	}

	private String getTreeValue(Bucket currentBucket) {
		return currentBucket
				.getValueForKey(fragmentKeyReference)
				.orElseThrow(
						() -> new MissingFragmentValueException(currentBucket.createPartialUrl(), fragmentKeyReference)
				);
	}
}
