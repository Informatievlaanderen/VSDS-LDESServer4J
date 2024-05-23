package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.FragmentationConfigEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DataConversionException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class FragmentationConfigEntityConverter implements AttributeConverter<List<FragmentationConfigEntity>, String> {
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final CollectionType collectionType = objectMapper.getTypeFactory()
			.constructCollectionType(List.class, FragmentationConfigEntity.class);

	@Override
	public String convertToDatabaseColumn(List<FragmentationConfigEntity> attribute) {
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new DataConversionException(FragmentationConfigEntity.class, true, e);
		}
	}

	@Override
	public List<FragmentationConfigEntity> convertToEntityAttribute(String dbData) {
		try {
			return objectMapper.readValue(dbData, collectionType);
		} catch (JsonProcessingException e) {
			throw new DataConversionException(FragmentationConfigEntity.class, false, e);
		}
	}
}
