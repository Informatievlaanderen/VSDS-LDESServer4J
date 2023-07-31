package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentSequence;

import java.util.Optional;

public interface FragmentSequenceRepository {

	Optional<FragmentSequence> findLastProcessedSequence(ViewName viewName);

	void saveLastProcessedSequence(FragmentSequence sequence);

}
