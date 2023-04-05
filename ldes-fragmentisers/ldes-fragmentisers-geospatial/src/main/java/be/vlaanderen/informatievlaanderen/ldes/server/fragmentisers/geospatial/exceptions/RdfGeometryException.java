package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.exceptions;

import org.apache.jena.geosparql.implementation.GeometryWrapper;

public class RdfGeometryException extends RuntimeException {
	private final GeometryWrapper geometryWrapper;
	private final String srsFormat;

	public RdfGeometryException(GeometryWrapper geometryWrapper, String srsFormat) {
		super();
		this.geometryWrapper = geometryWrapper;
		this.srsFormat = srsFormat;
	}

	@Override
	public String getMessage() {
		return "unable to convert member" + geometryWrapper + " to " + srsFormat + "format";
	}
}
