package be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;

public enum WellKnownPrefix {
	FOAF("foaf", "http://xmlns.com/foaf/0.1/"),
	RDF("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
	RDFS("rdfs", "http://www.w3.org/2000/01/rdf-schema#"),
	SKOS("skos", "http://www.w3.org/2004/02/skos/core#"),
	OWL("owl", "http://www.w3.org/2002/07/owl#"),
	XSD("xsd", "http://www.w3.org/2001/XMLSchema#"),
	GEO("geo", "http://www.opengis.net/ont/geosparql#"),
	DCAT("dcat", "http://www.w3.org/ns/dcat#"),
	DCT("dct", "http://purl.org/dc/terms/"),
	PROV("prov", "http://www.w3.org/ns/prov#"),
	M8G("m8g", "http://data.europa.eu/m8g/"),
	TREE("tree", "https://w3id.org/tree#"),
	LDES("ldes", "https://w3id.org/ldes#"),
	SH("sh", "http://www.w3.org/ns/shacl#"),
	SHSH("shsh", "http://www.w3.org/ns/shacl-shacl#");

	private final String prefix;
	private final String uri;

	WellKnownPrefix(String prefix, String uri) {
		this.prefix = prefix;
		this.uri = uri;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getUri() {
		return uri;
	}
}