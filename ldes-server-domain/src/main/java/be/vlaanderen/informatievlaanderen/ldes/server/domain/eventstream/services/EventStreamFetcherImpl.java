package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.stereotype.Component;

@Component
public class EventStreamFetcherImpl implements EventStreamFetcher {
	private final LdesConfig ldesConfig;
	private final ViewConfig viewConfig;

	public EventStreamFetcherImpl(LdesConfig ldesConfig, ViewConfig viewConfig) {
		this.ldesConfig = ldesConfig;
		this.viewConfig = viewConfig;
	}

	@Override
	public EventStream fetchEventStream() {
		return new EventStream(ldesConfig.getCollectionName(), ldesConfig.getTimestampPath(), ldesConfig.getVersionOf(),
				ldesConfig.getShape(), viewConfig.getViews().stream().map(ViewSpecification::getName).toList());
	}
}
