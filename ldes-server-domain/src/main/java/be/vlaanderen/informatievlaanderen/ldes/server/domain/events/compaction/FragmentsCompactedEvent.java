package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.compaction;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;

public record FragmentsCompactedEvent(LdesFragmentIdentifier firstFragment, LdesFragmentIdentifier secondFragment) {


}
