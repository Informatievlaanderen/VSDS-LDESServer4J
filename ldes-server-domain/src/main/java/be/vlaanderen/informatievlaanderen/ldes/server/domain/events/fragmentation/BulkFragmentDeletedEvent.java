package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;

import java.util.Set;

public record BulkFragmentDeletedEvent(Set<LdesFragmentIdentifier> ldesFragmentIdentifiers) {
}
