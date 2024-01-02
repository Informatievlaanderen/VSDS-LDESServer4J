package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.compaction;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;

import java.util.List;

public record FragmentsCompactedEvent(List<LdesFragmentIdentifier> compactedFragments) {
}
