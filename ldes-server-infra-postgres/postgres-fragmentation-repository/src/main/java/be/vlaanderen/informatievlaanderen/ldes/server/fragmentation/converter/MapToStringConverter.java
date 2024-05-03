package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.Map;

@Converter
public class MapToStringConverter implements AttributeConverter<Map<String, String>, String> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Map<String, String> attribute) {
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to convert map to string", e);
		}
	}

	@Override
	public Map<String, String> convertToEntityAttribute(String dbData) {
		try {
			return objectMapper.readValue(dbData, Map.class);
		} catch (IOException e) {
			throw new RuntimeException("Failed to convert string to map", e);
		}
	}
}