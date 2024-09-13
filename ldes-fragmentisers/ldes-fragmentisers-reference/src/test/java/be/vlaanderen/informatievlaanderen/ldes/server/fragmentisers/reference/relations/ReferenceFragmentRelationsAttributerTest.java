package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.ReferenceFragmentationStrategyWrapper.DEFAULT_FRAGMENTATION_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations.ReferenceFragmentRelationsAttributer.TREE_REFERENCE_EQUALS_RELATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class ReferenceFragmentRelationsAttributerTest {

    private static final String FRAGMENT_REFERENCE =
            "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel";
    private static final ViewName viewName = new ViewName("collectionName", "view");
    private static final Bucket parentBucket = new Bucket(BucketDescriptor.empty(), viewName);

    private final String fragmentationPath = RDF.type.getURI();

    private ReferenceFragmentRelationsAttributer relationsAttributer;

    @BeforeEach
    void setUp() {
        relationsAttributer =
                new ReferenceFragmentRelationsAttributer(fragmentationPath, DEFAULT_FRAGMENTATION_KEY);
    }

    @Test
    void when_ReferenceFragmentsAreCreated_RelationsBetweenRootAndCreatedFragmentsAreAdded() {
        Bucket rootBucket = createReferenceBuket("");
        Bucket referenceBucket = createReferenceBuket(FRAGMENT_REFERENCE);
        BucketRelation expectedRelation = new BucketRelation(
                TREE_REFERENCE_EQUALS_RELATION,
                "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel",
                XSDDatatype.XSDanyURI.getURI(),
                fragmentationPath
                );

        relationsAttributer.addRelationFromRootToBottom(rootBucket, referenceBucket);

        assertThat(rootBucket.getChildren())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(referenceBucket.withRelation(expectedRelation));
    }

    @Test
    void when_UnknownReferenceFragmentsAreCreated_RelationsBetweenRootAndCreatedFragmentsAreAdded() {
        Bucket rootBucket = createReferenceBuket("");
        Bucket referenceBucket = createReferenceBuket("unknown");

        relationsAttributer.addDefaultRelation(rootBucket, referenceBucket);

        assertThat(rootBucket.getChildren())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(referenceBucket.withGenericRelation());
    }

    @Test
    void when_ReferenceFragmentHasNoReference_ThenAnExceptionIsThrown() {
        Bucket rootBucket = createReferenceBuket("");
        Bucket referenceBucket = parentBucket.createChild(new BucketDescriptorPair("invalid-key", FRAGMENT_REFERENCE));

        assertThatExceptionOfType(MissingFragmentValueException.class)
                .isThrownBy(() -> relationsAttributer.addRelationFromRootToBottom(rootBucket, referenceBucket))
                .withMessage("FragmentId /collectionName/view?invalid-key=https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceeldoes not contain a value for fragmentkey reference");
    }

    private Bucket createReferenceBuket(String reference) {
        return parentBucket.createChild(new BucketDescriptorPair(DEFAULT_FRAGMENTATION_KEY, reference));
    }

}
