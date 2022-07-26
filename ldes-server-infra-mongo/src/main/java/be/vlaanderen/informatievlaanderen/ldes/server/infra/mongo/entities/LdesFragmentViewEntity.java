package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.entities.LdesFragmentView;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ldesfragmentview")
public class LdesFragmentViewEntity {

    @Id
    private final String fragmentId;
    private final String content;

    public LdesFragmentViewEntity(String fragmentId, String content) {
        this.fragmentId = fragmentId;
        this.content = content;
    }

    public static LdesFragmentViewEntity fromLdesFragmentView(LdesFragmentView ldesFragmentView) {
        return new LdesFragmentViewEntity(ldesFragmentView.getFragmentId(), ldesFragmentView.getContent());
    }

    public LdesFragmentView toLdesFragmentView() {
        return new LdesFragmentView(this.fragmentId, content, true);
    }
}
