package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.ReferenceFragmentationStrategyWrapper.DEFAULT_FRAGMENTATION_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations.ReferenceFragmentRelationsAttributer.TREE_REFERENCE_EQUALS_RELATION;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReferenceFragmentRelationsAttributerTest {

    private static final String fragmentReference =
            "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel";
    private static final ViewName viewName = new ViewName("collectionName", "view");
    private static final Fragment parentFragment = new Fragment(new LdesFragmentIdentifier(viewName, List.of()));;

    @Mock
    private FragmentRepository fragmentRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private final String fragmentationPath = RDF.type.getURI();

    private ReferenceFragmentRelationsAttributer relationsAttributer;

    @BeforeEach
    void setUp() {
        relationsAttributer =
                new ReferenceFragmentRelationsAttributer(applicationEventPublisher, fragmentRepository, fragmentationPath, DEFAULT_FRAGMENTATION_KEY);
    }

    @Test
    void when_ReferenceFragmentsAreCreated_RelationsBetweenRootAndCreatedFragmentsAreAdded() {
        Fragment rootFragment = createReferenceFragment("");
        Fragment referenceFragment = createReferenceFragment(fragmentReference);
        TreeRelation expectedRelation = new TreeRelation(fragmentationPath,
                LdesFragmentIdentifier.fromFragmentId("/collectionName/view?reference=https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel"),
                "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel",
                XSDDatatype.XSDanyURI.getURI(),
                TREE_REFERENCE_EQUALS_RELATION);

        relationsAttributer.addRelationsFromRootToBottom(rootFragment, referenceFragment);

        assertTrue(rootFragment.containsRelation(expectedRelation));
        verify(fragmentRepository, times(1)).saveFragment(rootFragment);
    }

    @Test
    void when_ReferenceFragmentHasNoReference_ThenAnExceptionIsThrown() {
        Fragment rootFragment = createReferenceFragment("");
        Fragment referenceFragment = parentFragment.createChild(new FragmentPair("invalid-key", fragmentReference));

        assertThatExceptionOfType(MissingFragmentValueException.class)
                .isThrownBy(() -> relationsAttributer.addRelationsFromRootToBottom(rootFragment, referenceFragment))
                .withMessage("FragmentId /collectionName/view?invalid-key=https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceeldoes not contain a value for fragmentkey reference");
    }

    private Fragment createReferenceFragment(String reference) {
        return parentFragment.createChild(new FragmentPair(DEFAULT_FRAGMENTATION_KEY, reference));
    }

}
