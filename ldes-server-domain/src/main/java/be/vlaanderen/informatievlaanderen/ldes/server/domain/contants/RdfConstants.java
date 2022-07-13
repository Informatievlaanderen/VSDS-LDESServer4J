package be.vlaanderen.informatievlaanderen.ldes.server.domain.contants;

import org.apache.jena.rdf.model.Property;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class RdfConstants {
    private RdfConstants() {
    }

    public static final String TREE = "https://w3id.org/tree#";
    public static final Property TREE_VIEW = createProperty(TREE, "view");
    public static final Property TREE_SHAPE = createProperty(TREE, "shape");
    public static final Property TREE_RELATION = createProperty(TREE, "relation");
    public static final Property TREE_VALUE = createProperty(TREE, "value");
    public static final Property TREE_PATH = createProperty(TREE, "path");
    public static final Property TREE_NODE = createProperty(TREE, "node");
    public static final Property TREE_MEMBER = createProperty(TREE, "member");
    public static final String LDES = "https://w3id.org/ldes#";
    public static final Property LDES_VERSION_OF = createProperty(LDES, "versionOf");
    public static final String VERSION_OF_URI = "http://purl.org/dc/terms/isVersionOf";
    public static final Property LDES_TIMESTAMP_PATH = createProperty(LDES, "timestampPath");
    public static final String RDF_SYNTAX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final Property RDF_SYNTAX_TYPE = createProperty(RDF_SYNTAX, "type");
    public static final String LDES_EVENT_STREAM_URI = "https://w3id.org/ldes#EventStream";
    public static final String PROV_GENERATED_AT_TIME = "http://www.w3.org/ns/prov#generatedAtTime";
}
