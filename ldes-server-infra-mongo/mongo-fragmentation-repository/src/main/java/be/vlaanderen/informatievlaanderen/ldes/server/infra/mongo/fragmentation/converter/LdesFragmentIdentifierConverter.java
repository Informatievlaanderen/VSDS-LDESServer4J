package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class LdesFragmentIdentifierConverter implements Converter<String, LdesFragmentIdentifier> {

	@Override
	public LdesFragmentIdentifier convert(String fragmentId) {
		return LdesFragmentIdentifier.fromFragmentId(fragmentId);
	}

}
