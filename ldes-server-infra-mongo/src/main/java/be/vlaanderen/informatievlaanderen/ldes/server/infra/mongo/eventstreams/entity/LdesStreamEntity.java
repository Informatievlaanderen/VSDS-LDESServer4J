package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstreams.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("ldesstream")
public class LdesStreamEntity {
    @Id
    private final String collection;
    private final String timestampPath;
    private final String versionOfPath;
    private final String shape;
    private final List<String> viewNames;

    public LdesStreamEntity(String collection, String timestampPath, String versionOfPath, String shape, List<String> viewNames) {
        this.collection = collection;
        this.timestampPath = timestampPath;
        this.versionOfPath = versionOfPath;
        this.shape = shape;
        this.viewNames = viewNames;
    }

    public EventStream toEventStream() {
        return new EventStream(collection, timestampPath, versionOfPath, shape, views);
    }

    public static LdesStreamEntity fromEventStream(EventStream eventStream) {
        return new LdesStreamEntity(
                eventStream.collection(),
                eventStream.timestampPath(),
                eventStream.versionOfPath(),
                eventStream.shape(),
                eventStream.views()
        );
    }
}
