package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.exceptions.RdfGeometryException;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.vocabulary.SRS_URI;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GeospatialBucketiser {
	private final GeospatialConfig geospatialConfig;

	public GeospatialBucketiser(GeospatialConfig geospatialConfig) {
		this.geospatialConfig = geospatialConfig;
	}

	public Set<String> bucketise(Member member) {
		List<Coordinate> coordinates = new ArrayList<>();

		member.getFragmentationObjects(geospatialConfig.fragmenterSubjectFilter(),
				geospatialConfig.fragmenterProperty())
				.stream()
				.map(GeometryWrapper.class::cast)
				.map(geometryWrapper -> {
					try {
						return geometryWrapper.convertSRS(SRS_URI.WGS84_CRS);
					} catch (FactoryException | TransformException e) {
						throw new RdfGeometryException(geometryWrapper, SRS_URI.WGS84_CRS);
					}
				})
				.forEach(geometryWrapper -> coordinates.addAll(
						List.of(geometryWrapper.getXYGeometry().getCoordinates())));
		return coordinates.stream()
				.map(coordinate -> CoordinateToTileStringConverter.convertCoordinate(coordinate,
						geospatialConfig.maxZoomLevel()))
				.collect(Collectors.toSet());
	}
}
