package be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageNumber;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageTest {

	@Test
	void given_NumberlessPage_when_CreateChildPage_then_PageHasNumber() {
		final var numberLessPage = Page.createNumberLessPage(null, null);

		final var childPage = numberLessPage.createChildPage();

		assertThat(childPage.getPageNumber()).contains(PageNumber.startPageNumber());
	}
}