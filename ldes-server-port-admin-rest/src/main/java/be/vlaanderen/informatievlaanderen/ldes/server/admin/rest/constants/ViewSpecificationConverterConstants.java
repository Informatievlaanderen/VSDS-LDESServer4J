package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.constants;

public class ViewSpecificationConverterConstants {
	public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String LDES = "https://w3id.org/ldes#";
	public static final String TREE = "https://w3id.org/tree#";
	public static final String CUSTOM = "http://example.org/";

	public static final String TYPE_PREDICATE = RDF + "type";
	public static final String RETENTION_TYPE = LDES + "retentionPolicy";
	public static final String RETENTION_OBJECT = LDES + "retentionPolicy";
	public static final String FRAGMENTATION_TYPE = CUSTOM + "Fragmentation";
	public static final String FRAGMENTATION_OBJECT = CUSTOM + "fragmentationStrategy";
	public static final String VIEW_TYPE_OBJECT = TREE + "viewDescription";

	private ViewSpecificationConverterConstants() {
	}
}
