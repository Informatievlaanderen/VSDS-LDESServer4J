package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

public class LdesMember {
    private final String[] quads;

    public LdesMember(final String[] quads) {
        this.quads = quads;
    }

    public String[] getQuads() {
        return this.quads;
    }

}
