package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.bucketising.ReferenceBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceFragmentCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceFragmentCreator.FRAGMENT_KEY_REFERENCE_ROOT;


public class ReferenceFragmentationStrategy extends FragmentationStrategyDecorator {

    public static final String REFERENCE_FRAGMENTATION = "ReferenceFragmentation";

    private final ReferenceBucketiser referenceBucketiser;
    private final ReferenceFragmentCreator fragmentCreator;
    private final ObservationRegistry observationRegistry;

    private Fragment rootFragment = null;

    public ReferenceFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
                                          ReferenceBucketiser referenceBucketiser,
                                          ReferenceFragmentCreator fragmentCreator,
                                          ObservationRegistry observationRegistry,
                                          FragmentRepository fragmentRepository) {
        super(fragmentationStrategy, fragmentRepository);
        this.referenceBucketiser = referenceBucketiser;
        this.fragmentCreator = fragmentCreator;
        this.observationRegistry = observationRegistry;
    }

    @Override
    public void addMemberToFragment(Fragment parentFragment, String memberId, Model memberModel,
                                    Observation parentObservation) {
        // TODO TVB: 18/01/24 https://telraam-api.net/ldes/observations/by-location?tile=0/0/0
        final var fragmentationObservation = startObservation(parentObservation);
        getRootFragment(parentFragment);
        referenceBucketiser.bucketise(memberModel)
                .parallelStream()
                .map(reference -> fragmentCreator.getOrCreateFragment(parentFragment, reference, rootFragment))
                .forEach(ldesFragment -> super.addMemberToFragment(ldesFragment, memberId, memberModel, fragmentationObservation));
        fragmentationObservation.stop();
    }

    private Observation startObservation(Observation parentObservation) {
        return Observation.createNotStarted("reference fragmentation",
                        observationRegistry)
                .parentObservation(parentObservation)
                .start();
    }

    private void getRootFragment(Fragment parentFragment) {
        if (rootFragment == null) {
            // TODO TVB: 18/01/24 root configureerbaar?
            Fragment referenceRootFragment = fragmentCreator.getOrCreateRootFragment(parentFragment, FRAGMENT_KEY_REFERENCE_ROOT);
            super.addRelationFromParentToChild(parentFragment, referenceRootFragment);
            rootFragment = referenceRootFragment;
        }
    }

}
