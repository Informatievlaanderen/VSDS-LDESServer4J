package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config;

public record GeospatialConfig(String fragmenterSubjectFilter, String bucketiserProperty, int maxZoomLevel, String projection) {}