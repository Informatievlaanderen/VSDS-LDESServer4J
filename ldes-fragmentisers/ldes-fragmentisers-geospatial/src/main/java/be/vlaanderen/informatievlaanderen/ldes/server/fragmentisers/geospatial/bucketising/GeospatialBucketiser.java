package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.List;
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
		List<Coordinate> coordinates = new ArrayList<>();

		member.getFragmentationObjects(geospatialConfig.fragmenterSubjectFilter(), geospatialConfig.fragmenterProperty())
				.stream()
				.map(o -> (GeometryWrapper) o)
				.forEach(geometryWrapper -> coordinates.addAll(
						List.of(geometryWrapper.getXYGeometry().getCoordinates())));
		return coordinates.stream()
				.map(coordinateConverter::convertCoordinate)
				.map(coordinate -> CoordinateToTileStringConverter.convertCoordinate(coordinate,
						geospatialConfig.maxZoomLevel()))
				.collect(Collectors.toSet());
	}
}
