package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.hierarchy.bucketising;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.Bucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.hierarchy.config.HierarchyGeospatialConfig;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class HierarchyGeospatialBucketiser implements Bucketiser {
	private final HierarchyGeospatialConfig geospatialConfig;
	private final CoordinateConverter coordinateConverter;
	private final HierarchyCoordinateToTileStringConverter coordinateToTileStringConverter;

	public HierarchyGeospatialBucketiser(HierarchyGeospatialConfig geospatialConfig, CoordinateConverter coordinateConverter,
										 HierarchyCoordinateToTileStringConverter coordinateToTileStringConverter) {
		this.geospatialConfig = geospatialConfig;
		this.coordinateConverter = coordinateConverter;
		this.coordinateToTileStringConverter = coordinateToTileStringConverter;
	}

	@Override
	public Set<String> bucketise(LdesMember member) {
		GeometryWrapper wrapper = (GeometryWrapper) member
				.getFragmentationObject(geospatialConfig.getBucketiserProperty());
		return Arrays.stream(wrapper.getXYGeometry().getCoordinates())
				.map(coordinateConverter::convertCoordinate)
				.map(coordinate -> coordinateToTileStringConverter.convertCoordinate(coordinate,
						geospatialConfig.getMaxZoomLevel()))
				.collect(Collectors.toSet());
	}
}
