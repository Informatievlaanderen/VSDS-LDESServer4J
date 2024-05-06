package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.List;

@Converter
public class FragmentPairConverter implements AttributeConverter<List<FragmentPair>, String> {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final CollectionType collectionType = objectMapper.getTypeFactory()
			.constructCollectionType(List.class, FragmentPair.class);

	@Override
	public String convertToDatabaseColumn(List<FragmentPair> attribute) {
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error serializing fragmentations", e);
		}
	}

	@Override
	public List<FragmentPair> convertToEntityAttribute(String dbData) {
		try {
			return objectMapper.readValue(dbData, collectionType);
		} catch (IOException e) {
			throw new RuntimeException("Failed to convert string to map", e);
		}
	}
}