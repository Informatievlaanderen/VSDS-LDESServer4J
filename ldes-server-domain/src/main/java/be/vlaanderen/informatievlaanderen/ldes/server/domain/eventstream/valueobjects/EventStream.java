package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects;

import java.util.List;

public record EventStream(String collection, String timestampPath, String versionOf, String shape, List<String> views) {}
