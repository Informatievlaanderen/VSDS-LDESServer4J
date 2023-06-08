package be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;

import org.apache.jena.rdf.model.Property;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class RdfConstants {
	private RdfConstants() {
	}

	public static final String TREE = "https://w3id.org/tree#";
	public static final Property TREE_VIEW = createProperty(TREE, "view");
	public static final String TREE_VIEW_DESCRIPTION = TREE + "viewDescription";
	public static final String TREE_VIEW_DESCRIPTION_RESOURCE = TREE + "ViewDescription";
	public static final Property TREE_SHAPE = createProperty(TREE, "shape");
	public static final Property TREE_RELATION = createProperty(TREE, "relation");
	public static final Property TREE_VALUE = createProperty(TREE, "value");
	public static final Property TREE_PATH = createProperty(TREE, "path");
	public static final Property TREE_NODE = createProperty(TREE, "node");
	public static final Property TREE_MEMBER = createProperty(TREE, "member");
	public static final String TREE_NODE_RESOURCE = TREE + "Node";
	public static final String LDES = "https://w3id.org/ldes#";
	public static final String SHACL = "http://www.w3.org/ns/shacl#";
	public static final Property LDES_VERSION_OF = createProperty(LDES, "versionOfPath");
	public static final Property LDES_TIMESTAMP_PATH = createProperty(LDES, "timestampPath");
	public static final String RDF_SYNTAX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final Property RDF_SYNTAX_TYPE = createProperty(RDF_SYNTAX, "type");
	public static final String LDES_EVENT_STREAM_URI = "https://w3id.org/ldes#EventStream";
	public static final String GENERATED_AT_TIME = "generatedAtTime";
	public static final Property IS_PART_OF_PROPERTY = createProperty("http://purl.org/dc/terms/isPartOf");
	public static final String PROV = "http://www.w3.org/ns/prov#";
	public static final String PROV_GENERATED_AT_TIME = PROV + GENERATED_AT_TIME;

	public static final String TREE_GREATER_THAN_OR_EQUAL_TO_RELATION = TREE + "GreaterThanOrEqualToRelation";
	public static final String TREE_LESSER_THAN_OR_EQUAL_TO_RELATION = TREE + "LessThanOrEqualToRelation";

	public static final String GENERIC_TREE_RELATION = TREE + "Relation";

	public static final String VIEW = TREE + "view";
	public static final String VIEW_TYPE = LDES + "View";
	public static final String SHAPE = TREE + "shape";
	public static final String NODE_SHAPE_TYPE = SHACL + "NodeShape";
	public static final String EVENT_STREAM_TYPE = LDES + "EventStream";
	public static final String RETENTION_TYPE = LDES + "retentionPolicy";

	public static final String CUSTOM = "http://example.org/";
	public static final String FRAGMENTATION_TYPE = CUSTOM + "Fragmentation";
	public static final String FRAGMENTATION_OBJECT = TREE + "fragmentationStrategy";

}
