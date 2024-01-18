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


public class ReferenceFragmentationStrategy extends FragmentationStrategyDecorator {
    public static final String REFERENCE_FRAGMENTATION = "ReferenceFragmentation";
    private final ReferenceBucketiser referenceBucketiser;
    private final ReferenceFragmentCreator fragmentCreator;
    private final ObservationRegistry observationRegistry;

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
        final var fragmentationObservation = startObservation(parentObservation);
        referenceBucketiser.bucketise(memberModel)
                .parallelStream()
                .map(reference -> fragmentCreator.getOrCreateFragment(parentFragment, reference))
                .forEach(ldesFragment -> super.addMemberToFragment(ldesFragment, memberId, memberModel, fragmentationObservation));
        fragmentationObservation.stop();
    }

    private Observation startObservation(Observation parentObservation) {
        return Observation.createNotStarted("reference fragmentation",
                        observationRegistry)
                .parentObservation(parentObservation)
                .start();
    }

}
