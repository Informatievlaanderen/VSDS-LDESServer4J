package be.vlaanderen.informatievlaanderen.ldes.server.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber.NumericPageNumber;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber.PageNumber;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber.UuidPageNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageNumberParserTest {
	@Test
	void test_CreateNumericPageNumber() {
		final String pageNumberString = "pageNumber=2";

		final PageNumber pageNumber = PageNumberParser.parse(pageNumberString);

		assertThat(pageNumber).isEqualTo(new NumericPageNumber(2));
	}

	@Test
	void test_UuidNumericPageNumber() {
		final String pageNumberString = "pageNumber=26929514-237c-11ed-861d-0242ac120002";

		final PageNumber pageNumber = PageNumberParser.parse(pageNumberString);

		assertThat(pageNumber).isEqualTo(UuidPageNumber.fromString("26929514-237c-11ed-861d-0242ac120002"));
	}

	@ParameterizedTest(name = "{0}")
	@CsvSource(textBlock = """
			2,Invalid page number: 2 - Expected format: pageNumber=<INTEGER/UUID>
			pageNumber=a,Invalid UUID string: a,
			pageNumber=26929514-237c-11ed-861d,Invalid UUID string: 26929514-237c-11ed-861d
			pageNumber=26929514-237c-11ed-861d-237c-11ed-861d,UUID string too large
			""")
	void test_InvalidCreation(String pageNumberString, String expectedErrorMessage) {
		assertThatThrownBy(() -> PageNumberParser.parse(pageNumberString))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage(expectedErrorMessage);
	}
}