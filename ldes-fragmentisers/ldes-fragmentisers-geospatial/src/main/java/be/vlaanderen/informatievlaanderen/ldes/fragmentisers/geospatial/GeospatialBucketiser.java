package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.Bucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;

public class GeospatialBucketiser implements Bucketiser {
	
	private final GeospatialConfig geospatialConfig;
	
	public GeospatialBucketiser(GeospatialConfig geospatialConfig) {
		this.geospatialConfig = geospatialConfig;
	}

	@Override
	public String bucketise(LdesMember member) {
		GeometryWrapper wrapper = WKTDatatype.INSTANCE.parse("Polygon((-63.37716 45.97597,-71.05923 45.97597,-71.05923 41.65926,-63.37716 41.65926,-63.37716 45.97597))");
		
		// TODO Auto-generated method stub
		return null;
	}
}
