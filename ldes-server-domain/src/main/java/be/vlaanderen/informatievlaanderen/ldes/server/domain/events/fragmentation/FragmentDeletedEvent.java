package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;

public record FragmentDeletedEvent(LdesFragmentIdentifier ldesFragmentIdentifier) {
}
