package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects;

import java.util.List;

public class EventStream {
	private final String collection;
	private final String timestampPath;
	private final String versionOf;
	private final String shape;
	private final List<String> views;

	public EventStream(String collection, String timestampPath, String versionOf, String shape, List<String> views) {
		this.collection = collection;
		this.timestampPath = timestampPath;
		this.versionOf = versionOf;
		this.shape = shape;
		this.views = views;
	}

	public String getCollection() {
		return collection;
	}

	public String getTimestampPath() {
		return timestampPath;
	}

	public String getVersionOf() {
		return versionOf;
	}

	public String getShape() {
		return shape;
	}

	public List<String> getViews() {
		return views;
	}
}
