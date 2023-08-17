package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

public class RootFragmentRetriever {

	private final FragmentRepository fragmentRepository;
	private final ObservationRegistry observationRegistry;
	private Fragment rootFragment;

	public RootFragmentRetriever(FragmentRepository fragmentRepository, ObservationRegistry observationRegistry) {
		this.fragmentRepository = fragmentRepository;
		this.observationRegistry = observationRegistry;
	}

	Fragment retrieveRootFragmentOfView(ViewName viewName, Observation parentObservation) {
		Observation rootRetrievalObservation = Observation
				.createNotStarted("retrieve root of view " + viewName, observationRegistry)
				.parentObservation(parentObservation).start();

		if (rootFragment == null) {
			rootFragment = fragmentRepository
					.retrieveRootFragment(viewName.asString())
					.orElseThrow(() -> new MissingRootFragmentException(viewName.asString()));

		}

		rootRetrievalObservation.stop();
		return rootFragment;
	}

}
