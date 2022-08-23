package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

public class GeospatialFragmentNamingStrategy implements LdesFragmentNamingStrategy {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	@Override
	public FragmentPair getFragmentationValue() {
		String fragmentationValue = LocalDateTime.now().format(formatter);

		return new FragmentPair(FRAGMENT_KEY_TILE, fragmentationValue);
	}
}
