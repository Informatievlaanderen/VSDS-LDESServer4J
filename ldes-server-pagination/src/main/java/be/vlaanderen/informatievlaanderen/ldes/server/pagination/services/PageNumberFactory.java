package be.vlaanderen.informatievlaanderen.ldes.server.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber.NumericPageNumber;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber.PageNumber;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber.UuidPageNumber;
import org.apache.commons.lang3.math.NumberUtils;

import static be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber.PageNumber.PAGE_NUMBER;

public class PageNumberFactory {
	public static PageNumber createPageNumber(String pageNumberString) {
		if (!pageNumberString.contains(PAGE_NUMBER)) {
			throw new IllegalArgumentException("Invalid page number: %s - Expected format: %s=<INTEGER/UUID>".formatted(pageNumberString, PAGE_NUMBER));
		}
		final String value = pageNumberString.replace(PAGE_NUMBER + "=", "");
		if(NumberUtils.isCreatable(value)) {
			return new NumericPageNumber(Integer.parseInt(value));
		}
		return UuidPageNumber.fromString(value);
	}

	private PageNumberFactory() {}
}
