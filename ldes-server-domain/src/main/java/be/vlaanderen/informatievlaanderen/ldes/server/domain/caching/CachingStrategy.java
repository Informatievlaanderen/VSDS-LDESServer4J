package be.vlaanderen.informatievlaanderen.ldes.server.domain.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

public interface CachingStrategy {

	String generateCacheIdentifier(EventStream eventStream);

	String generateCacheIdentifier(LdesFragment ldesFragment);
}
