package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.EventStreamInfoResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching.TreeNodeInfoResponse;

import java.util.List;
import java.util.Objects;

public final class EventStreamResponse {
	private final EventStreamInfoResponse eventStreamInfoResponse;
	private final List<TreeNodeInfoResponse> views;

	public EventStreamResponse(EventStreamInfoResponse eventStreamInfoResponse,
			List<TreeNodeInfoResponse> views) {
		this.eventStreamInfoResponse = eventStreamInfoResponse;
		this.views = views;
	}

	public EventStreamInfoResponse eventStreamInfoResponse() {
		return eventStreamInfoResponse;
	}

	public List<TreeNodeInfoResponse> views() {
		return views;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof EventStreamResponse that))
			return false;
		return Objects.equals(eventStreamInfoResponse, that.eventStreamInfoResponse)
				&& Objects.equals(views, that.views);
	}

	@Override
	public int hashCode() {
		return Objects.hash(eventStreamInfoResponse, views);
	}

	public String getCacheIdentifier() {
		return eventStreamInfoResponse.getEventStreamId();
	}
}
