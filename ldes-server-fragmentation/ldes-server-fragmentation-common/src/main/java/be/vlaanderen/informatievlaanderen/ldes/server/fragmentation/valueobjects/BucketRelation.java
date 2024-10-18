package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

public record BucketRelation(String fromPartialUrl, String toPartialUrl, TreeRelation relation) {
}