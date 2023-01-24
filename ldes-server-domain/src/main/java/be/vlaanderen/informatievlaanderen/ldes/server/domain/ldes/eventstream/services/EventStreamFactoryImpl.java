package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.stereotype.Component;

@Component
public class EventStreamFactoryImpl implements EventStreamFactory {

	private final LdesConfig ldesConfig;
	private final ViewConfig viewConfig;

	public EventStreamFactoryImpl(LdesConfig ldesConfig, ViewConfig viewConfig) {
		this.ldesConfig = ldesConfig;
		this.viewConfig = viewConfig;
	}

	@Override
	public EventStream getEventStream() {
		return new EventStream(ldesConfig.getCollectionName(), ldesConfig.getTimestampPath(),
				ldesConfig.getVersionOfPath(),
				ldesConfig.validation().getShape(), viewConfig.getViews().stream().map(ViewSpecification::getName).toList());
	}
}
