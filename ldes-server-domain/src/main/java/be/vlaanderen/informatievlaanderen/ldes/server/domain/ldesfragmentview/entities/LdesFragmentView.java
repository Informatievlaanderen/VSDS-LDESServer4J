package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.entities;

public class LdesFragmentView {

    private final String fragmentId;
    private final String content;
    private final boolean immutable;

    public LdesFragmentView(String fragmentId, String content, boolean immutable) {
        this.fragmentId = fragmentId;
        this.content = content;
        this.immutable = immutable;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public String getFragmentId() {
        return fragmentId;
    }

    public String getContent() {
        return content;
    }
}
