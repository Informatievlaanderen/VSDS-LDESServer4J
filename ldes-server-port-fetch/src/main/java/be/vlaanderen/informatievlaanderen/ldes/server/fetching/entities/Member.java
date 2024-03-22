package be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;

import org.apache.jena.rdf.model.Model;

public record Member(String id, Model model) {

    public String getMemberIdWithoutPrefix() {
        if (id.startsWith("http")) {
            throw new IllegalStateException("id '%s' does not contain a prefix".formatted(id));
        }
        return id.substring(id.indexOf("/") + 1);
    }
}
