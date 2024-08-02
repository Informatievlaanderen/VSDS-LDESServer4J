package be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;

import org.apache.jena.rdf.model.Property;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class RdfConstants {
	private RdfConstants() {
	}

	public static final String TREE = "https://w3id.org/tree#";
	public static final Property TREE_VIEW = createProperty(TREE, "view");
	public static final String TREE_VIEW_DESCRIPTION = TREE + "viewDescription";
	public static final String TREE_PAGESIZE = TREE + "pageSize";
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
	public static final String RDF_SCHEMA = "http://www.w3.org/2000/01/rdf-schema#";
	public static final Property LDES_VERSION_OF = createProperty(LDES, "versionOfPath");
	public static final Property LDES_TIMESTAMP_PATH = createProperty(LDES, "timestampPath");
	public static final Property LDES_CREATE_VERSIONS = createProperty(LDES, "createVersions");
	public static final Property LDES_EVENT_SOURCE = createProperty(LDES, "eventSource");
	public static final Property LDES_SKOLEMIZATION_DOMAIN = createProperty(LDES, "skolemizationDomain");
	public static final String RDF_SYNTAX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final Property RDF_SYNTAX_TYPE = createProperty(RDF_SYNTAX, "type");
	public static final String LDES_EVENT_STREAM_URI = "https://w3id.org/ldes#EventStream";
	public static final String LDES_EVENT_SOURCE_URI = "https://w3id.org/ldes#EventSource";
	public static final String GENERATED_AT_TIME = "generatedAtTime";
	public static final String DC_TERMS = "http://purl.org/dc/terms/";
	public static final Property IS_PART_OF_PROPERTY = createProperty(DC_TERMS, "isPartOf");
	public static final Property DC_TERMS_IDENTIFIER = createProperty(DC_TERMS, "identifier");
	public static final String RDF_LITERAL = RDF_SCHEMA + "Literal";
	public static final Property DC_CONFORMS_TO = createProperty(DC_TERMS, "conformsTo");
	public static final Property DC_STANDARD = createProperty(DC_TERMS, "Standard");


	public static final String GENERIC_TREE_RELATION = TREE + "Relation";

	public static final String VIEW = TREE + "view";
	public static final String NODE_SHAPE_TYPE = SHACL + "NodeShape";
	public static final String EVENT_STREAM_TYPE = LDES + "EventStream";
	public static final String RETENTION_TYPE = LDES + "retentionPolicy";

	public static final String FRAGMENTATION_TYPE = TREE + "Fragmentation";
	public static final String FRAGMENTATION_OBJECT = TREE + "fragmentationStrategy";
	public static final String TREE_REMAINING_ITEMS = TREE + "remainingItems";

	public static final String SHACL_SOURCE_CONSTRAINT_COMPONENT = SHACL + "SPARQLConstraintComponent";
}
