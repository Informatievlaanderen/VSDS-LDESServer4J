package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = true)
public class LdesFragmentIdentifierConverter implements AttributeConverter<LdesFragmentIdentifier, String> {

	@Override
	public String convertToDatabaseColumn(LdesFragmentIdentifier ldesFragmentIdentifier) {
		return ldesFragmentIdentifier.asDecodedFragmentId();
	}

	@Override
	public LdesFragmentIdentifier convertToEntityAttribute(String fragmentId) {
		return LdesFragmentIdentifier.fromFragmentId(fragmentId);
	}
}
