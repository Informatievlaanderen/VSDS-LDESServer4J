package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE;

public class GeospatialConstants {
	private GeospatialConstants() {
	}

	public static final String FRAGMENT_KEY_TILE = "tile";
	public static final String FRAGMENT_KEY_TILE_ROOT = "0/0/0";

	public static final String GEOSPARQL_AS_WKT = "http://www.opengis.net/ont/geosparql#asWKT";

	public static final String TREE_GEOSPATIALLY_CONTAINS_RELATION = TREE + "GeospatiallyContainsRelation";

	public static final String WGS_84 = "<http://www.opengis.net/def/crs/OGC/1.3/CRS84>";
	public static final String WKT_DATA_TYPE = "http://www.opengis.net/ont/geosparql#wktLiteral";

}
