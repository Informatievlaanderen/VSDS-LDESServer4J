package be.vlaanderen.informatievlaanderen.ldes.server.retention.entities;

public class Member {
    private final String id;
    private final String collectionName;
    private final String versionOf;
    private final String timestamp;

    public Member(String id, String collectionName, String versionOf, String timestamp) {
        this.id = id;
        this.collectionName = collectionName;
        this.versionOf = versionOf;
        this.timestamp = timestamp;
    }
}
