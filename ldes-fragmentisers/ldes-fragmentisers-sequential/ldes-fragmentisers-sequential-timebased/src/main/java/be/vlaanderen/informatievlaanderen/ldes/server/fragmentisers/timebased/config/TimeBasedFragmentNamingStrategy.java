package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;

public class TimeBasedFragmentNamingStrategy implements LdesFragmentNamingStrategy {
	
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	@Override
	public FragmentPair getFragmentationValue() {
		String fragmentationValue = LocalDateTime.now().format(formatter);
		
		return new FragmentPair(GENERATED_AT_TIME, fragmentationValue);
	}
}
