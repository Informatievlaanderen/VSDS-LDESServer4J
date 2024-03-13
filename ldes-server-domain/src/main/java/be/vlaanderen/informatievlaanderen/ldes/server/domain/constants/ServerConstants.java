package be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;

import org.apache.jena.riot.Lang;

import java.util.List;

public class ServerConstants {
    private ServerConstants() {
    }

    public static final String DEFAULT_BUCKET_STRING = "unknown";
    public static final List<Lang> RELATIVE_URL_INCOMPATIBLE_LANGS = List.of(
            Lang.NQUADS,
            Lang.NTRIPLES,
            Lang.RDFXML,
            Lang.RDFJSON,
            Lang.RDFPROTO,
            Lang.RDFTHRIFT
    );
}
