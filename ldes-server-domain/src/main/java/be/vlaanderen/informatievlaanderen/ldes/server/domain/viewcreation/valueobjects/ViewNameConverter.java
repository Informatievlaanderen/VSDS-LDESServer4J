package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

// TODO TVB: 16/08/23 this can be deleted?
@Component
@ConfigurationPropertiesBinding
public class ViewNameConverter implements Converter<String, ViewName> {

	@Override
	public ViewName convert(String viewName) {
		return new ViewName(null, viewName);
	}

}
