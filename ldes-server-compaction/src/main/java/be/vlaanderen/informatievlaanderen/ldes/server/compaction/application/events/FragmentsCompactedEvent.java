package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.events;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

public record FragmentsCompactedEvent(Fragment firstFragment, Fragment secondFragment) {


}
