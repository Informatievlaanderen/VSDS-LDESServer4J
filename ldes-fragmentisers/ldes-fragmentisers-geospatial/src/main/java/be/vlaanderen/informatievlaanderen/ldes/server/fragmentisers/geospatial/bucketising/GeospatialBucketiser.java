package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import org.apache.jena.geosparql.implementation.GeometryWrapper;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class GeospatialBucketiser {
	private final GeospatialConfig geospatialConfig;
	private final CoordinateConverter coordinateConverter;

	public GeospatialBucketiser(GeospatialConfig geospatialConfig,
			CoordinateConverter coordinateConverter) {
		this.geospatialConfig = geospatialConfig;
		this.coordinateConverter = coordinateConverter;
	}

	public Set<String> bucketise(LdesMember member) {
		GeometryWrapper wrapper = (GeometryWrapper) member
				.getFragmentationObject(geospatialConfig.getBucketiserProperty());
		return Arrays.stream(wrapper.getXYGeometry().getCoordinates())
				.map(coordinateConverter::convertCoordinate)
				.map(coordinate -> CoordinateToTileStringConverter.convertCoordinate(coordinate,
						geospatialConfig.getMaxZoomLevel()))
				.collect(Collectors.toSet());
	}
}
